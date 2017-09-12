package okeanos.dao;

import java.util.List;

import org.sql2o.Connection;

import okeanos.model.FfessmLicence;

public class FfessmLicenceDao {

	public static List<FfessmLicence> getAllItems() {
		String sql = "SELECT id, fk_saison_id, label, price FROM ffessm_licence";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).executeAndFetch(FfessmLicence.class);
		}
	}

	public static List<FfessmLicence> getAllItemsForSaison(Long saisonId) {
		String sql = "SELECT id, fk_saison_id, label, price FROM ffessm_licence WHERE fk_saison_id = :saisonId";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("saisonId", saisonId).executeAndFetch(FfessmLicence.class);
		}
	}

	public static FfessmLicence getItemById(Long id) {
		String sql = "SELECT id, fk_saison_id, label, price FROM ffessm_licence WHERE id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(FfessmLicence.class);
		}
	}

	public static FfessmLicence save(FfessmLicence item) {

		if (item == null || "".equals(item.getLabel())) {
			System.out.println("Error : cannot save empty item !");
			return item;
		}

		if (item.getId() == null) { // Mode création
			System.out.println("Création d'un item : " + item.getLabel());
			String sql = "insert into ffessm_licence (fk_saison_id, label, price) values (:fk_saison_id, :label, :price)";

			try (Connection con = Sql2oDao.sql2o.open()) {
				Long insertedId = (Long) con.createQuery(sql, true).addParameter("fk_saison_id", item.getFk_saison_id())
						.addParameter("label", item.getLabel()).addParameter("price", item.getPrice()).executeUpdate()
						.getKey();
				System.out.println("ID généré : " + insertedId);
				return getItemById(insertedId);
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println(e.getMessage());
				e.printStackTrace();
				return item;
			}

		} else { // Mode modification
			System.out.println("Mise à jour d'un item : " + item.getLabel());
			String sql = "update ffessm_licence set fk_saison_id = :fk_saison_id, label = :label, price = :price where id = :id";

			try (Connection con = Sql2oDao.sql2o.open()) {
				con.createQuery(sql).bind(item).executeUpdate();
			}
			return getItemById(item.getId());

		}
	}

	public static Boolean deleteItem(Long id) {
		System.out.println("Suppression d'un item : " + id);
		String sql = "delete from ffessm_licence where id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			con.createQuery(sql).addParameter("id", id).executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
