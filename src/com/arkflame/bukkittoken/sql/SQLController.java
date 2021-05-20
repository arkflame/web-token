package com.arkflame.bukkittoken.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Cointains all the logic for the SQLConnection
class SQLController {
    boolean hasColumn(final Connection connection, final String column) {
        try {
            DatabaseMetaData md = connection.getMetaData();
            ResultSet rs = md.getColumns(null, null, "users", column);

            if (rs.next()) {
                return true;
            }
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    void addColumn(final Connection connection, final String column) {
        try {
            final PreparedStatement stmt = connection.prepareStatement("ALTER TABLE users ? ? INT NULL");

            stmt.setString(1, hasColumn(connection, column) ? "MODIFY" : "ADD");
            stmt.setString(2, column);
            stmt.executeUpdate();
        } catch (final SQLException e) {
            e.printStackTrace();
        }
    }

    int getToken(final Connection connection, final String nickname) {
        try {
            final PreparedStatement stmt = connection.prepareStatement("SELECT users WHERE nickname = ?");

            stmt.setString(1, nickname);

            final ResultSet result = stmt.executeQuery();
            final int token = result.getInt("bukkit_token");

            return token;
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }

    boolean hasToken(final Connection connection, final String nickname) {
        return getToken(connection, nickname) > 0;
    }

    int generateToken(final Connection connection, final String nickname) {
        try {
            final PreparedStatement stmt = connection
                    .prepareStatement("? users SET bukkit_token = ? WHERE nickname = ?");
            final int token = (int) (999999999 * Math.random());

            stmt.setString(1, hasToken(connection, nickname) ? "UPDATE" : "INSERT");
            stmt.setInt(2, token);
            stmt.setString(3, nickname);
            stmt.executeUpdate();

            return token;
        } catch (final SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
