package com.lingoflow.android.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.lingoflow.android.data.entity.SettingsEntity;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class SettingsDao_Impl implements SettingsDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<SettingsEntity> __insertionAdapterOfSettingsEntity;

  private final SharedSQLiteStatement __preparedStmtOfIncrementScore;

  public SettingsDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfSettingsEntity = new EntityInsertionAdapter<SettingsEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `settings` (`id`,`theme`,`modelType`,`cloudModel`,`practiceLanguage`,`uiLanguage`,`score`) VALUES (?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final SettingsEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTheme());
        statement.bindString(3, entity.getModelType());
        statement.bindString(4, entity.getCloudModel());
        statement.bindString(5, entity.getPracticeLanguage());
        statement.bindString(6, entity.getUiLanguage());
        statement.bindLong(7, entity.getScore());
      }
    };
    this.__preparedStmtOfIncrementScore = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE settings SET score = score + ? WHERE id = 1";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final SettingsEntity settings,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfSettingsEntity.insert(settings);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementScore(final int delta, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementScore.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, delta);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementScore.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<SettingsEntity> observe() {
    final String _sql = "SELECT * FROM settings WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"settings"}, new Callable<SettingsEntity>() {
      @Override
      @Nullable
      public SettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTheme = CursorUtil.getColumnIndexOrThrow(_cursor, "theme");
          final int _cursorIndexOfModelType = CursorUtil.getColumnIndexOrThrow(_cursor, "modelType");
          final int _cursorIndexOfCloudModel = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudModel");
          final int _cursorIndexOfPracticeLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "practiceLanguage");
          final int _cursorIndexOfUiLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "uiLanguage");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final SettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpTheme;
            _tmpTheme = _cursor.getString(_cursorIndexOfTheme);
            final String _tmpModelType;
            _tmpModelType = _cursor.getString(_cursorIndexOfModelType);
            final String _tmpCloudModel;
            _tmpCloudModel = _cursor.getString(_cursorIndexOfCloudModel);
            final String _tmpPracticeLanguage;
            _tmpPracticeLanguage = _cursor.getString(_cursorIndexOfPracticeLanguage);
            final String _tmpUiLanguage;
            _tmpUiLanguage = _cursor.getString(_cursorIndexOfUiLanguage);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            _result = new SettingsEntity(_tmpId,_tmpTheme,_tmpModelType,_tmpCloudModel,_tmpPracticeLanguage,_tmpUiLanguage,_tmpScore);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object get(final Continuation<? super SettingsEntity> $completion) {
    final String _sql = "SELECT * FROM settings WHERE id = 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<SettingsEntity>() {
      @Override
      @Nullable
      public SettingsEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTheme = CursorUtil.getColumnIndexOrThrow(_cursor, "theme");
          final int _cursorIndexOfModelType = CursorUtil.getColumnIndexOrThrow(_cursor, "modelType");
          final int _cursorIndexOfCloudModel = CursorUtil.getColumnIndexOrThrow(_cursor, "cloudModel");
          final int _cursorIndexOfPracticeLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "practiceLanguage");
          final int _cursorIndexOfUiLanguage = CursorUtil.getColumnIndexOrThrow(_cursor, "uiLanguage");
          final int _cursorIndexOfScore = CursorUtil.getColumnIndexOrThrow(_cursor, "score");
          final SettingsEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpTheme;
            _tmpTheme = _cursor.getString(_cursorIndexOfTheme);
            final String _tmpModelType;
            _tmpModelType = _cursor.getString(_cursorIndexOfModelType);
            final String _tmpCloudModel;
            _tmpCloudModel = _cursor.getString(_cursorIndexOfCloudModel);
            final String _tmpPracticeLanguage;
            _tmpPracticeLanguage = _cursor.getString(_cursorIndexOfPracticeLanguage);
            final String _tmpUiLanguage;
            _tmpUiLanguage = _cursor.getString(_cursorIndexOfUiLanguage);
            final int _tmpScore;
            _tmpScore = _cursor.getInt(_cursorIndexOfScore);
            _result = new SettingsEntity(_tmpId,_tmpTheme,_tmpModelType,_tmpCloudModel,_tmpPracticeLanguage,_tmpUiLanguage,_tmpScore);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
