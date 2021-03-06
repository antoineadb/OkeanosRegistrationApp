package okeanos.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

import okeanos.model.HockeyTeam;

public class HockeyTeamDao {
	private static Logger logger = LoggerFactory.getLogger(HockeyTeamDao.class);

	public static List<HockeyTeam> getAllItems() {
		String sql = "SELECT id, label FROM hockey_team ORDER BY label";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).executeAndFetch(HockeyTeam.class);
		}
	}

	public static HockeyTeam getItemById(Long id) {
		String sql = "SELECT id, label FROM hockey_team WHERE id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(HockeyTeam.class);
		}
	}

	public static HockeyTeam newItem(String label) {
		String sql = "insert into hockey_team (label) values (:label)";

		try (Connection con = Sql2oDao.sql2o.open()) {
			Long insertedId = (Long) con.createQuery(sql, true).addParameter("label", label).executeUpdate().getKey();
			return getItemById(insertedId);
		}
	}

	public static HockeyTeam save(HockeyTeam item) {

		if (item == null || "".equals(item.getLabel())) {
			logger.error("Error : cannot save empty item !");
			return null;
		}

		if (item.getId() == null) { // Mode création
			logger.info("Création d'un item : " + item.getLabel());
			String sql = "insert into hockey_team (label) values (:label)";

			try (Connection con = Sql2oDao.sql2o.open()) {
				Long insertedId = (Long) con.createQuery(sql, true).addParameter("label", item.getLabel())
						.executeUpdate().getKey();
				logger.debug("ID généré : " + insertedId);
				return getItemById(insertedId);
			}

		} else { // Mode modification
			logger.info("Mise à jour d'un item : " + item.getLabel());
			String sql = "update hockey_team set label = :label where id = :id";

			try (Connection con = Sql2oDao.sql2o.open()) {
				con.createQuery(sql).bind(item).executeUpdate();
			}
			return getItemById(item.getId());

		}
	}

	public static Boolean deleteItem(Long id) {
		logger.info("Suppression d'un item : " + id);
		String sql = "delete from hockey_team where id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			con.createQuery(sql).addParameter("id", id).executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
