package okeanos.dao;

import java.util.List;

import org.sql2o.Connection;

import okeanos.model.Account;

public class AccountDao {

	public static List<Account> getAllItems(Boolean getPass) {
		String sql = null;
		if (getPass)
			sql = "SELECT id, mail, salt, password, admin FROM account ORDER BY mail";
		else
			sql = "SELECT id, mail, admin FROM account";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).executeAndFetch(Account.class);
		}
	}

	public static Account getItemById(Long id, Boolean getPass) {
		String sql = null;
		if (getPass)
			sql = "SELECT id, mail, salt, password, admin FROM account WHERE id = :id";
		else
			sql = "SELECT id, mail, admin FROM account WHERE id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(Account.class);
		}
	}

	public static Account getItemByMail(String mail) {
		String sql = "SELECT id, mail, salt, password, admin FROM account WHERE mail = :mail";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("mail", mail).executeAndFetchFirst(Account.class);
		}
	}

	public static Account newItem(String mail, String salt, String password, Boolean admin) {
		String sql = "insert into account (mail, salt, password, admin) values (:mail, :salt, :password, :admin)";

		try (Connection con = Sql2oDao.sql2o.open()) {
			Long insertedId = (Long) con.createQuery(sql, true).addParameter("mail", mail).addParameter("salt", salt)
					.addParameter("password", password).addParameter("admin", admin).executeUpdate().getKey();
			return getItemById(insertedId, false);
		}
	}

	public static Account updateItem(Account item) {
		String sql = "update account set mail = :mail, admin = :admin where id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			con.createQuery(sql).bind(item).executeUpdate();
		}

		return item;
	}

	public static Account updatePassword(Account item) {
		String sql = "update account set salt = :salt, password = :password where id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			con.createQuery(sql).bind(item).executeUpdate();
		}

		return item;
	}

	public static Boolean deleteItem(Long id) {
		String sql = "delete from account where id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			con.createQuery(sql).addParameter("id", id).executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
