package com.student.finance.data.local

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS accounts (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, name TEXT NOT NULL, isActive INTEGER NOT NULL DEFAULT 0)")
        db.execSQL("INSERT INTO accounts (id, name, isActive) VALUES (1, 'Akun Utama', 1)")
        db.execSQL("ALTER TABLE transactions ADD COLUMN accountId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE categories ADD COLUMN accountId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE budgets ADD COLUMN accountId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE saving_goals ADD COLUMN accountId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE debts ADD COLUMN accountId INTEGER NOT NULL DEFAULT 1")
        db.execSQL("ALTER TABLE reminders ADD COLUMN accountId INTEGER NOT NULL DEFAULT 1")
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE categories ADD COLUMN isCustom INTEGER NOT NULL DEFAULT 0")
    }
}
