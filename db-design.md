# DB設計書

## 1. 目的
このDB設計書は、ToDoアプリで使うデータベースの表を決めるためのものです。  
ここでの DB は「データベース（情報をためる場所）」の意味です。

## 2. 対象テーブル
このアプリで使うテーブルは `todos` の 1 つだけです。  
テーブルは「データを行と列で保存する表」です。

## 3. テーブル定義

### 3-1. `todos` テーブル
| カラム名 | 型 | NULL | 初期値 | 制約 | 説明 | 対応する要件 |
|---|---|---|---|---|---|---|
| `id` | `BIGINT` | NO | なし | 主キー（1件を見分ける番号）, 自動採番（自動で番号をふる） | 1件を区別する番号 | 要件 2-1 の `id` |
| `title` | `VARCHAR(255)` | NO | なし | なし | やること | 要件 2-1 の `title`、要件 5 の「やることは空欄にしない」「255文字以内」 |
| `detail` | `VARCHAR(255)` | YES | NULL | なし | メモ | 要件 2-1 の `detail`、要件 5 の「メモは255文字以内」 |
| `category` | `VARCHAR(255)` | NO | なし | なし | ジャンル | 要件 2-1 の `category`、要件 2-2 のジャンル5つ、要件 5 の「5つから選ぶ」 |
| `priority` | `INT` | NO | なし | なし | 優先度 | 要件 2-1 の `priority`、要件 4-1 の表示変換、要件 4-2 の入力、要件 5 の「選択肢から選ぶ」 |
| `due_date` | `DATE` | YES | NULL | なし | 期限 | 要件 2-1 の `due_date`、要件 4-2 の入力、要件 5 の「日付として入力する」 |
| `completed` | `BOOLEAN` | NO | `false` | なし | 完了かどうか | 要件 2-1 の `completed`、要件 4-1 の表示変換 |
| `created_at` | `DATETIME` | NO | 現在日時 | なし | 登録日時 | 要件 2-1 の `created_at` |
| `updated_at` | `DATETIME` | NO | 現在日時 | なし | 更新日時 | 要件 2-1 の `updated_at` |

### 3-2. 補足
- `NULL` は「値が入っていない状態」です。
- `NOT NULL` は「空にできない」という意味です。
- 主キーは「1件を一意に見分けるための番号」です。
- 自動採番は「登録のたびに自動で番号をふること」です。

## 4. ジャンルの値
`category` に入れられる値は次の 5 つです。

- デザイン
- マーケティング
- プログラミング
- 就活
- 生活改善

## 5. ルール
### 5-1. `id`
`id` は自動で作られる。
手入力はしない。

### 5-2. `created_at` と `updated_at`
`created_at` と `updated_at` は、DBが自動で日時を入れる。

### 5-3. `title`
`title` は必須である。
`title` は 255文字以内である。

### 5-4. `detail`
`detail` は任意である。
`detail` は 255文字以内である。

### 5-5. `category`
`category` は必須である。
`category` は 5つの値から選ぶ。

### 5-6. `priority`
`priority` は必須である。
`priority` は 1, 2, 3 のいずれかである。

### 5-7. `due_date`
`due_date` は任意である。
`due_date` は日付として保存する。

### 5-8. `completed`
`completed` は必須である。
`completed` の初期値は `false` である。
`false` は「未完了」の意味である。

## 6. CREATE TABLE 文
以下は `todos` テーブルを作るための DDL（表を作る命令文）です。

```sql
CREATE TABLE todos (
    id BIGINT NOT NULL AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    detail VARCHAR(255),
    category VARCHAR(255) NOT NULL,
    priority INT NOT NULL,
    due_date DATE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);
```

## 7. 要件との対応まとめ
| 要件 | 対応カラム |
|---|---|
| やること | `title` |
| メモ | `detail` |
| ジャンル | `category` |
| 優先度 | `priority` |
| 期限 | `due_date` |
| 完了かどうか | `completed` |
| 登録日時 | `created_at` |
| 更新日時 | `updated_at` |
| 1件を区別する番号 | `id` |

## 8. 補足
この設計書は、`requirements.md` と `handout_app_spec.md` をもとに作成している。  
ここでは DB の設計だけをまとめており、画面の設計は含めていない。
