package okeanos.dao;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sql2o.Connection;

import okeanos.model.AdherentDocument;

public class AdherentDocumentDao {
	private static Logger logger = LoggerFactory.getLogger(AdherentDocumentDao.class);

	public static List<AdherentDocument> getAllItems() {
		String sql = "SELECT id, file_type, content_disposition, data FROM adherent_document";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).executeAndFetch(AdherentDocument.class);
		}
	}

	public static AdherentDocument getItemById(Long id) {
		String sql = "SELECT id, file_type, content_disposition, data FROM adherent_document WHERE id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			return con.createQuery(sql).addParameter("id", id).executeAndFetchFirst(AdherentDocument.class);
		}
	}

	public static AdherentDocument save(AdherentDocument item) {

		if (item == null || "".equals(item.getFile_type()) || "".equals(item.getContent_disposition())) {
			logger.error("Error : cannot save empty item !");
			return null;
		}

		if (item.getId() == null) { // Mode création
			logger.info("Création d'un item : " + item.getContent_disposition());
			String sql = "insert into adherent_document (file_type, content_disposition, data) values (:file_type, :content_disposition, :data)";

			try (Connection con = Sql2oDao.sql2o.open()) {
				Long insertedId = (Long) con.createQuery(sql, true).addParameter("file_type", item.getFile_type())
						.addParameter("content_disposition", item.getContent_disposition())
						.addParameter("data", item.getData()).executeUpdate().getKey();
				logger.debug("ID généré : " + insertedId);
				return getItemById(insertedId);
			}

		} else { // Mode modification
			logger.info("Mise à jour d'un item : " + item.getContent_disposition());
			String sql = "update adherent_document set file_type = :file_type, content_disposition = :content_disposition, data = :data where id = :id";

			try (Connection con = Sql2oDao.sql2o.open()) {
				con.createQuery(sql).bind(item).executeUpdate();
			}
			return getItemById(item.getId());

		}
	}

	public static Boolean deleteItem(Long id) {
		logger.info("Suppression d'un item : " + id);
		String sql = "delete from adherent_document where id = :id";

		try (Connection con = Sql2oDao.sql2o.open()) {
			con.createQuery(sql).addParameter("id", id).executeUpdate();
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
