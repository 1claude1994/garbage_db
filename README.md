# 🗑️ 渋谷区 ごみ情報データ基盤

渋谷区の公式Webサイトおよび公開データをもとに、  
ごみの品目、分別方法、収集地域などの情報を収集・整理・正規化し、  
サービスで利用できるデータとして提供するためのデータ処理プロジェクトです。

## 📌 プロジェクト概要

自治体のごみに関する情報は、Webページ、PDF、CSV、APIなど、  
さまざまな形式・データソースに分散しています。

本プロジェクトでは、これらのデータを収集し、  
元データを保持した上でParserによるデータ抽出を行い、  
正規化したデータをPostgreSQLに保存します。

最終的には、ユーザーが自分の住所や地域を入力することで、  
その地域に対応したごみの分別方法や収集情報を確認できる  
「ごみの百科事典」サービスの構築を目指します。

## 🎯 主な目的

- 渋谷区の公式データの収集
- Webページ / PDF / CSV / APIなど、複数形式のデータ処理
- 取得した元データ（Raw Data）の保持
- HTMLおよび文書データのParsing
- データ表現の違いを吸収する正規化処理
- ごみの種類や地域情報の標準化
- データの変更・更新を追跡できる構造の構築
- サービスで利用可能な標準データの作成
- 将来的な他自治体への拡張を考慮したデータ構造の設計

## 🏗️ データ処理フロー

```text
自治体・公開データ
       │
       ▼
┌─────────────────┐
│ Data Collection │
│ Web / PDF / CSV │
│ API             │
└────────┬────────┘
         ▼
┌─────────────────┐
│      Raw        │
│ 元データの保持   │
└────────┬────────┘
         ▼
┌─────────────────┐
│     Parser      │
│ HTML / PDFなど  │
│ データ抽出       │
└────────┬────────┘
         ▼
┌─────────────────┐
│  Normalization  │
│ データの標準化   │
│ Mapping         │
└────────┬────────┘
         ▼
┌─────────────────┐
│ Master / Service│
│ サービス用データ │
└────────┬────────┘
         ▼
      ユーザーサービス
```

## 🗄️ Database Structure

PostgreSQLを使用し、データの処理段階ごとにSchemaを分離して管理しています。

```text
garbage_db
│
├── raw
│   ├── source
│   ├── document
│   ├── api_source
│   ├── import_batch
│   └── raw_record
│
├── master
│   ├── waste_category
│   ├── area
│   └── item
│
├── normalization
│   ├── field_mapping
│   └── mapping_rule
│
└── service
    └── disposal_rule
```

### Raw

外部から取得したデータを、処理前の段階で保持・管理します。

- `source` : データソースの管理
- `document` : 取得対象となる文書の管理
- `api_source` : APIデータソースの管理
- `import_batch` : データ取得処理単位の管理
- `raw_record` : Parserによって抽出された元データの保存

### Normalization

データソースごとに異なる表現を、サービスで利用できる共通形式に変換します。

例えば、以下のような異なる表現を統一します。

```text
燃やすごみ
可燃ごみ
燃えるごみ
燃やすゴミ
      │
      ▼
  BURNABLE
```

`mapping_rule`を使用して、元データの値と標準化された値をマッピングします。

また、`field_mapping`を使用して、データソースごとに異なるフィールド名を共通フィールド名へ変換します。

```text
ごみの品目     → item_name
インデックス   → item_index
説明           → description
GIS搭載用住所 → gis_address
```

### Master

サービス全体で共通して使用する基準データを管理します。

例：

```text
BURNABLE
NON_BURNABLE
RESOURCE
OVERSIZED
```

### Service

正規化されたデータをもとに、実際のサービスで利用する情報を管理します。

例：

```text
可燃ごみとして出してください
        ↓
    BURNABLE
        ↓
収集可否・排出ルール
```

## 🔎 HTML Parsing

Webページ形式のデータについては、JavaのHTML Parserである **Jsoup** を使用して処理します。

```text
HTML
 │
 ▼
Jsoup
 │
 ▼
HTML DOM
 │
 ├── <table>
 │     └── <tr>
 │           ├── <td>
 │           └── <td>
 │
 ▼
必要なElementを抽出
 │
 ▼
JSON
 │
 ▼
Raw Data
```

Jsoupを使用してHTMLをDOM構造として解析し、`<tr>`や`<td>`などのElementを検索・取得します。

Parserでは、取得したデータをプロジェクトで定義したフィールド構造に合わせてJSON形式に変換します。

## 🧩 Technology Stack

### Backend

- Java
- Spring
- Spring Data JPA
- Hibernate

### Database

- PostgreSQL

### Data Processing

- Jsoup
- JSON
- PDF / CSV / API Data Processing

### Development

- Eclipse
- Maven
- Git / GitHub

## 🔄 JPA & Hibernate

本プロジェクトでは、Spring Data JPAを使用してデータベースを操作しています。

```text
Spring Data JPA
       │
       ▼
      JPA
   （標準仕様）
       │
       ▼
   Hibernate
   （実装）
       │
       ▼
  PostgreSQL
```

JPAはJavaオブジェクトとデータベースを連携するための標準仕様を提供し、  
Hibernateがその仕様を実装して、SQLの生成やデータベースとの通信を行います。

## 📊 Data Processing Example

取得した元データ：

```text
ごみの品目 = 燃やすごみ
説明 = 可燃ごみとして出してください
GIS搭載用住所 = 渋谷区渋谷1丁目
```

### 1. Raw Data

取得した元データをJSON形式で保持します。

```json
{
  "ごみの品目": "燃やすごみ",
  "説明": "可燃ごみとして出してください",
  "GIS搭載用住所": "渋谷区渋谷1丁目"
}
```

### 2. Field Mapping

```text
ごみの品目
    ↓
item_name

説明
    ↓
description

GIS搭載用住所
    ↓
gis_address
```

### 3. Value Mapping

```text
燃やすごみ
    ↓
BURNABLE
```

### 4. Master / Service

標準化されたデータをもとに、サービスで利用するごみの種類や排出ルールを生成します。

## 🔐 Raw Dataと正規化データの分離

本プロジェクトでは、元データと正規化されたデータを分離して管理しています。

```text
外部データ
   ↓
Raw
   ↓
Parsing
   ↓
Normalization
   ↓
Master / Service
```

この構成により、取得時の元データを追跡できるようにし、  
正規化ルールを変更した場合でも、元データをもとに再処理できる構造を目指しています。

## 🚀 拡張性

現在は渋谷区を対象として開発していますが、  
特定の自治体に依存しないデータ構造を目指しています。

将来的には、

```text
渋谷区
新宿区
港区
世田谷区
...
```

のように、他の自治体のデータを追加できるよう、  
Source、Mapping Rule、Field Mapping、Areaなどの構造を分離して設計しています。

## 📁 Project Status

現在、以下の機能を中心に開発しています。

- [x] Rawデータ保存構造
- [x] Source / Document管理
- [x] Import Batch管理
- [x] Raw Record保存
- [x] Field Mapping
- [x] Value Mapping
- [x] Waste Category構築
- [x] Areaデータ構築
- [x] Disposal Rule構築
- [x] JPA / HibernateによるDB連携
- [ ] 複数データソースへのParser拡張
- [ ] データ変更検知・検証
- [ ] サービスAPI構築
- [ ] ユーザー住所によるごみ情報検索
- [ ] 他自治体へのデータ拡張

## 🎯 最終目標

「収集 → Parsing → Raw保存 → 正規化 → 検証 → サービスデータ生成」

という一連のデータパイプラインを構築し、

ユーザーが自分の地域に合わせて、

- ごみの種類
- 分別方法
- 収集曜日
- 収集地域
- 排出ルール
- 粗大ごみ情報

などを簡単に確認できる **「ごみの百科事典」** サービスの実現を目指します。
