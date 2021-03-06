package simugen.core.sql;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Builds a SQL Table.
 * 
 * @author Lorelei
 *
 */
public class DefaultTableBuilder
{
	private String tableName;

	private Map<String, ColumnType> mapColumnToType = new LinkedHashMap<>();

	/**
	 * @param tableName
	 *            The name of the table to build.
	 */
	public DefaultTableBuilder(String tableName)
	{
		this.tableName = tableName;
	}

	/**
	 * @param name
	 *            The name of the column to be added to the table.
	 * @param type
	 *            The {@link ColumnType} of the column to be added.
	 */
	public void addColumn(String name, ColumnType type)
	{
		mapColumnToType.put(name, type);
	}

	/**
	 * @param conn
	 *            The {@link Connection} with which to build the table.
	 */
	public void buildTable(Connection conn)
	{
		String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (\n";

		for (String name : mapColumnToType.keySet())
		{
			sql = sql + name + " " + mapColumnToType.get(name).getType()
					+ ",\n";
		}

		sql = sql.substring(0, sql.length() - 2);
		sql = sql + ");";

		try
		{
			Statement statement = conn.createStatement();

			System.out.println(sql);

			statement.execute(sql);
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * @param conn
	 *            The {@link Connection} in which to insert values.
	 * @param mapColumnsValues
	 *            The {@link Map} of <i>columns</i> to <i>values</i> to be
	 *            inserted.
	 */
	public void insert(Connection conn, Map<String, Object> mapColumnsValues)
	{
		assert mapColumnsValues.keySet().containsAll(mapColumnToType.keySet());

		String sql = "INSERT INTO " + tableName + "(";

		for (String column : mapColumnsValues.keySet())
		{
			sql = sql + column + ",";
		}

		sql = sql.substring(0, sql.length() - 1);
		sql = sql + ") VALUES(";

		for (int i = 0; i < mapColumnsValues.keySet().size(); i++)
		{
			sql = sql + "?,";
		}

		sql = sql.substring(0, sql.length() - 1);
		sql = sql + ")";

		try (PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			for (int i = 0; i < mapColumnsValues.keySet().size(); i++)
			{
				String column = (String) mapColumnsValues.keySet().toArray()[i];

				int j = i + 1;

				switch (mapColumnToType.get(column))
				{
				case STRING:
					pstmt.setString(j, (String) mapColumnsValues.get(column));
					break;
				case DOUBLE:
					pstmt.setDouble(j, (Double) mapColumnsValues.get(column));
					break;
				case INT:
					pstmt.setInt(j, (Integer) mapColumnsValues.get(column));
					break;
				case DATE:
					pstmt.setDate(j, (Date) mapColumnsValues.get(column));
					break;
				case TIME:
					pstmt.setTime(j, (Time) mapColumnsValues.get(column));
					break;
				case TIMESTAMP:
					pstmt.setTimestamp(j,
							(Timestamp) mapColumnsValues.get(column));
					break;
				default:
					break;
				}
			}

			System.out.println(pstmt.toString());
			pstmt.executeUpdate();
		}
		catch (SQLException e)
		{
			System.out.println(e.getMessage());
		}
	}
}
