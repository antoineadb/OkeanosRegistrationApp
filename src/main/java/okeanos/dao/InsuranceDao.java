package okeanos.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

import okeanos.model.Insurance;

public class InsuranceDao {
	private static Logger logger = LoggerFactory.getLogger(InsuranceDao.class);

	public static List<Insurance> getAllItems() {
		String sql = "SELECT id, fk_saison_id, label, price FROM insurance ORDER BY label";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).executeAndFetch(Insurance.class);
		}
	}

	public static List<Insurance> getAllItemsForSaison(Long saisonId) {
		String sql = "SELECT id, fk_saison_id, label, price FROM insurance WHERE fk_saison_id = :saisonId ORDER BY label";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("saisonId", saisonId).executeAndFetch(Insurance.class);
		}
	}

	public static Insurance getItemById(Long id) {
		String sql = "SELECT id, fk_saison_id, label, price FROM insurance WHERE id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(Insurance.class);
		}
	}

	public static Insurance save(Insurance item) {

		if (item == null || "".equals(item.getLabel())) {
			logger.error("Error : cannot save empty item !");
			return null;
		}

		if (item.getId() == null) { // Mode création
			logger.info("Création d'un item : " + item.getLabel());
			String sql = "insert into insurance (fk_saison_id, label, price) values (:fk_saison_id, :label, :price)";

			try (Connection con = Sql2oDao.sql2o.open()) {
				Long insertedId = (Long) con.createQuery(sql, true).addParameter("fk_saison_id", item.getFk_saison_id())
						.addParameter("label", item.getLabel()).addParameter("price", item.getPrice()).executeUpdate()
						.getKey();
				logger.debug("ID généré : " + insertedId);
				return getItemById(insertedId);
			}

		} else { // Mode modification
			logger.info("Mise à jour d'un item : " + item.getLabel());
			String sql = "update insurance set fk_saison_id = :fk_saison_id, label = :label, price = :price where id = :id";

			try (Connection con = Sql2oDao.sql2o.open()) {
				con.createQuery(sql).bind(item).executeUpdate();
			}
			return getItemById(item.getId());

		}
	}

	public static Boolean deleteItem(Long id) {
		logger.info("Suppression d'un item : " + id);
		String sql = "delete from insurance where id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			con.createQuery(sql).addParameter("id", id).executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
