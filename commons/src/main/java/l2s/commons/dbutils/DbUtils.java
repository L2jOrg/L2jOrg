package l2s.commons.dbutils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * A collection of JDBC helper methods.  This class is thread safe.
 */
public class DbUtils
{
	/**
	 * Close a <code>Connection</code>, avoid closing if null.
	 *
	 * @param conn Connection to close.
	 * @throws SQLException if a database access error occurs
	 */
	public static void close(Connection conn) throws SQLException
	{
		if(conn != null)
			conn.close();
	}

	/**
	 * Close a <code>ResultSet</code>, avoid closing if null.
	 *
	 * @param rs ResultSet to close.
	 * @throws SQLException if a database access error occurs
	 */
	public static void close(ResultSet rs) throws SQLException
	{
		if(rs != null)
			rs.close();
	}

	/**
	 * Close a <code>Statement</code>, avoid closing if null.
	 *
	 * @param stmt Statement to close.
	 * @throws SQLException if a database access error occurs
	 */
	public static void close(Statement stmt) throws SQLException
	{
		if(stmt != null)
			stmt.close();
	}

	/**
	 * Close a <code>Statement</code> and <code>ResultSet</code>, avoid closing if null.
	 *
	 * @param stmt Statement to close.
	 * @param rs ResultSet to close.
	 * @throws SQLException if a database access error occurs
	 */
	public static void close(Statement stmt, ResultSet rs) throws SQLException
	{
		close(stmt);
		close(rs);
	}

	/**
	 * Close a <code>Connection</code>, avoid closing if null and hide
	 * any SQLExceptions that occur.
	 *
	 * @param conn Connection to close.
	 */
	public static void closeQuietly(Connection conn)
	{
		try
		{
			close(conn);
		}
		catch(SQLException e)
		{
			// quiet
		}
	}

	/**
	 * Close a <code>Connection</code> and <code>Statement</code>.
	 * Avoid closing if null and hide any
	 * SQLExceptions that occur.
	 *
	 * @param conn Connection to close.
	 * @param stmt Statement to close.
	 */
	public static void closeQuietly(Connection conn, Statement stmt)
	{
		try
		{
			closeQuietly(stmt);
		}
		finally
		{
			closeQuietly(conn);
		}
	}

	/**
	 * Close a <code>Statement</code> and <code>ResultSet</code>.
	 * Avoid closing if null and hide any
	 * SQLExceptions that occur.
	 *
	 * @param stmt Statement to close.
	 * @param rs ResultSet to close.
	 */
	public static void closeQuietly(Statement stmt, ResultSet rs)
	{
		try
		{
			closeQuietly(stmt);
		}
		finally
		{
			closeQuietly(rs);
		}
	}

	/**
	 * Close a <code>Connection</code>, <code>Statement</code> and
	 * <code>ResultSet</code>.  Avoid closing if null and hide any
	 * SQLExceptions that occur.
	 *
	 * @param conn Connection to close.
	 * @param stmt Statement to close.
	 * @param rs ResultSet to close.
	 */
	public static void closeQuietly(Connection conn, Statement stmt, ResultSet rs)
	{

		try
		{
			closeQuietly(rs);
		}
		finally
		{
			try
			{
				closeQuietly(stmt);
			}
			finally
			{
				closeQuietly(conn);
			}
		}
	}

	/**
	 * Close a <code>ResultSet</code>, avoid closing if null and hide any
	 * SQLExceptions that occur.
	 *
	 * @param rs ResultSet to close.
	 */
	public static void closeQuietly(ResultSet rs)
	{
		try
		{
			close(rs);
		}
		catch(SQLException e)
		{
			// quiet
		}
	}

	/**
	 * Close a <code>Statement</code>, avoid closing if null and hide
	 * any SQLExceptions that occur.
	 *
	 * @param stmt Statement to close.
	 */
	public static void closeQuietly(Statement stmt)
	{
		try
		{
			close(stmt);
		}
		catch(SQLException e)
		{
			// quiet
		}
	}
}
