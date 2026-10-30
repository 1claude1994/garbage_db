# 🗑️ 渋谷区 ごみ情報データ基盤 - エンタープライズDBサーバー & データパイプライン

自治体（渋谷区）の公式Webサイトおよびオープンデータからごみ分別・収集情報を収集・正規化し、実サービスへ高信頼なデータを提供する**データパイプライン兼エン터プライズPostgreSQLデータベース基盤**です。

Jsoupによるスクレイピング・抽出処理に加え、**Rocky Linux 8.9（VMware環境）上でのPostgreSQLサーバー構築、`pg_hba.conf`による接続・認証制御、4層スキーマ分離設計（Raw/Normalization/Master/Service）**を実装。将来的なAWS RDSへのクラウド移行を見据えたインフラ設計を採用しています。

---

## 📌 プロジェクト概要

自治体のごみ分別情報は、Webページ、PDF、CSVなど多岐にわたる形式で分散しており、表記の揺らぎ（例：「燃やすごみ」「可燃ごみ」「燃えるごみ」）が存在します。

本プロジェクトでは、取得した元データ（Raw Data）の完全性を保持したままデータ抽出・マッピングを行い、正規化された標準データをPostgreSQL上に構築します。

### 主要インフラ・アーキテクチャ特徴
- **エンタープライズLinuxサーバー運用**: Rocky Linux 8.9環境におけるPostgreSQLインスタンスのチューニングおよびサービス運用
- **セキュアなDBアクセス制御**: `pg_hba.conf`およびMD5認証による接続元IP制限・認証ポリシーの適用
- **4段階スキーマ分離設計**: `raw` ➔ `normalization` ➔ `master` ➔ `service` によるデータ追跡性と再処理性の保証
- **高信頼データパイプライン**: JsoupによるDOMパース、JSON中間保持、JPA/Hibernateを通じたトランザクション処理
- **クラウドマイグレーション設計**: オンプレミス環境からAWS RDS（PostgreSQL）およびVPCプライベートサブネット構成への移行親和性

---

## 🖥️ インフラストラクチャ & ネットワーク構成

本データ基盤は、データ収集・バッチ処理を行うアプリケーションノードと、永続化・クエリ処理を担うRocky Linux DBノードを分離・連携させています。

```text
[ Data Source (渋谷区 Web/OpenData) ]
                   │
                   │ HTTP / HTTPS (Jsoup Crawler)
                   ▼
┌─────────────────────────────────────────────────────────────┐
│  Application & Processing Node                              │
│                                                             │
│  - Spring / Spring Data JPA                                 │
│  - Jsoup HTML Parser Engine                                 │
│  - Data Mapping & Normalization Logic                       │
└──────────────────────────────┬──────────────────────────────┘
                               │
                               │ TCP (Port 5432 / MD5 Auth)
                               ▼
┌─────────────────────────────────────────────────────────────┐
│  VMware Virtual Machine (Rocky Linux 8.9 : 192.168.0.250)   │
│                                                             │
│  [ OS & Network Interface ]                                 │
│  - OS: Rocky Linux 8.9 (Green Obsidian)                     │
│  - Interface: ens160 (192.168.0.250/24)                     │
│                                                             │
│  [ PostgreSQL Engine (Port: 5432) ]                         │
│  - listen_addresses = '*'                                   │
│  - Configuration: /var/lib/pgsql/data/pg_hba.conf           │
│                                                             │
│  ┌────────────────────────────────────────────────────────┐ │
│  │ Database: garbage_db (4-Tier Schema)                   │ │
│  │ ├── raw           : Raw Records & Import Batch         │ │
│  │ ├── normalization : Field Mapping & Value Rules        │ │
│  │ ├── master        : Unified Categories & Items         │ │
│  │ └── service       : Service-ready Disposal Rules       │ │
│  └────────────────────────────────────────────────────────┘ │
└─────────────────────────────────────────────────────────────┘
```

---

## ⚙️ インフラ環境 & 技術スタック

| レイヤー | 採用技術・環境 | 役割・詳細 |
|---|---|---|
| **OS / Virtualization** | Rocky Linux 8.9 / VMware | RHEL互換エンタープライズOS、VMware仮想ネットワーク構成 |
| **Network Interface** | `ens160` (192.168.0.250/24) | VMXNET3準拠の高速仮想ネットワークアダプター |
| **Database Server** | PostgreSQL 13+ | リレーショナルデータ保管、スキーマ分離、トランザクション制御 |
| **DB Access Control** | `pg_hba.conf` / MD5 | 接続元アドレス制御、クライアント認証の厳密化 |
| **Backend & ORM** | Java, Spring Data JPA, Hibernate | データ永続化層の制御、エンティティマッピング |
| **Data Processing** | Jsoup, JSON | 自治体HTMLテーブルのパース、非定型データのJSON中間化 |
| **Build & Management**| Maven, Git / GitHub | ビルド自動化およびインフラ・スキーマ定義の構成管理 |

---

## 🔒 データベースセキュリティ & アクセス制御 (`pg_hba.conf`)

PostgreSQLインスタンスへの不正アクセスを遮断し、安全なデータ通信を担保するため、`/var/lib/pgsql/data/pg_hba.conf`にて以下の認証・接続ポリシーを構成しています。

```text
# PostgreSQL Client Authentication Configuration (Excerpt)
# TYPE  DATABASE        USER            ADDRESS                 METHOD
local   all             all                                     md5
host    all             all             127.0.0.1/32            md5
host    all             all             0.0.0.0/0               md5
host    all             all             ::1/128                 md5
local   replication     all                                     peer
host    replication     all             127.0.0.1/32            ident
host    replication     all             ::1/128                 ident
```
- **listen_addresses**: 外部アプリケーションノードからの通信を受け付けるため `*` にバインド。
- **暗号化パスワード認証**: すべてのTCP接続に対して`md5`パスワードハッシュ認証を強制。
- **今後のアクセス制限強化**: 0.0.0.0/0（全開放）から、アプリケーションサーバーの固定IP（例: `192.168.0.25/32`）のみを許可するホワイトリスト制御へ順次移行。

---

## 🏗️ データ処理パイプライン概要

```text
自治体・公開データ (Web / HTML)
       │
       ▼
┌─────────────────────────────────┐
│ 1. Data Collection & Parsing    │ JsoupによるHTMLパース /
│    (HTML Table ➔ JSON)          │ 要素抽出
└────────────────┬────────────────┘
                 ▼
┌─────────────────────────────────┐
│ 2. Raw Schema Storage           │ 元データ・取得バッチの
│    (raw_record, import_batch)   │ 完全性を維持して保存
└────────────────┬────────────────┘
                 ▼
┌─────────────────────────────────┐
│ 3. Normalization Engine         │ フィールド統一・
│    (field_mapping, mapping_rule)│ 表記揺らぎの標準化マッピング
└────────────────┬────────────────┘
                 ▼
┌─────────────────────────────────┐
│ 4. Master & Service Schema      │ サービス提供用
│    (waste_category, item, rule) │ 最適化データの生成
└────────────────┬────────────────┘
                 ▼
      ユーザー向け検索サービス
```

---

## 🗄️ Database Schema Architecture (4層分離設計)

データの更新や自治体ルールの変更が発生した場合でも、元データを保持したまま再処理（Reprocessing）が可能なアーキテクチャを採用しています。

```text
garbage_db
│
├── raw             # 外部から取得した非加工データ
│   ├── source
│   ├── document
│   ├── api_source
│   ├── import_batch
│   └── raw_record
│
├── normalization   # データの標準化・マッピング定義
│   ├── field_mapping
│   └── mapping_rule
│
├── master          # サービス共通の基準マスター
│   ├── waste_category
│   ├── area
│   └── item
│
└── service         # サービス層から直接参照される提供用データ
    └── disposal_rule
```

### 1. Raw 層
外部から取得したデータを未加工の状態で保持・管理。
- `source`: データ提供元の管理
- `document`: 収集対象ドキュメントの管理
- `import_batch`: バッチ処理単位の実行履歴管理
- `raw_record`: パースされた元データのJSON保存

### 2. Normalization 層
自治体ごとに異なる表記揺らぎを標準定義に変換。
```text
燃やすごみ / 可燃ごみ / 燃えるごみ ──▶ BURNABLE
ごみの品目 ──▶ item_name
GIS搭載用住所 ──▶ gis_address
```

### 3. Master 層 & Service 層
サービス全体で統一された区分（`BURNABLE`, `RESOURCE` 等）を基に、最終的な排出ルールや収集スケジュールデータを構築。

---

## 🔎 HTML Parsing & JPA Entity Mapping

### HTML Parsing (Jsoup)
```text
HTML Table ──▶ Jsoup Node Select ──▶ JSON Object ──▶ Raw Entity ──▶ PostgreSQL
```
JsoupによりWebページのDOMツリーを走査し、`<tr>`, `<td>`タグから属性値を安全に抽出。構造化JSON形式に変換した上でDBへ投入します。

### Spring Data JPA & Hibernate
オブジェクト指向モデルとリレーショナルデータベースの整合性を維持し、トランザクション境界を管理。
```text
Spring Data JPA ──▶ Hibernate (SQL生成) ──▶ JDBC Driver ──▶ PostgreSQL (Port: 5432)
```

---

## 🚀 クラウド移行ロード맵 (AWS Migration Roadmap)

本オンプレミスデータ基盤をAWSクラウド環境へ段階的に移行する計画を策定しています。

```text
[ Current: On-Premises ]             [ Target: AWS Cloud Architecture ]
 Rocky Linux 8.9 (VMware)   ──────▶   Amazon VPC (Private Subnet)
 PostgreSQL Instance                  └── Amazon RDS for PostgreSQL (Multi-AZ)
 App Batch Worker                     └── AWS ECS / Fargate (Containerized Worker)
 Local Security Control               └── AWS Security Group (Port 5432 Restricted)
```

- [x] Rocky Linux 8.9上でのPostgreSQLサーバー構築および運用
- [x] `pg_hba.conf`およびネットワークポートのバインド設計
- [x] Raw/Normalization/Master/Service 4層スキーマの確立
- [x] JsoupおよびJPAによるデータ取り込み・変換パイプラインの実装
- [ ] **DBマイグレーション**: `pg_dump`を用いたスキーマ・データのAWS RDS (PostgreSQL) への移行
- [ ] **ネットワーク分離**: AWS VPC内のPrivate SubnetにRDSを配置し、EC2/ECSからのみアクセス可能とするセキュリティグループ設計
- [ ] **自動化**: データ更新バッチのEventBridge + ECSタスク化
