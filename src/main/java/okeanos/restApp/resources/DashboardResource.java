package okeanos.restApp.resources;

import static spark.Spark.get;

import java.util.ArrayList;
import java.util.HashMap;

import okeanos.dao.AccountDao;
import okeanos.dao.AdherentInfoDao;
import okeanos.dao.AdherentInfoSaisonDao;
import okeanos.dao.FfessmLicenceDao;
import okeanos.dao.InsuranceDao;
import okeanos.dao.SubscriptionDao;
import okeanos.model.Account;
import okeanos.model.AdherentInfo;
import okeanos.model.AdherentInfoSaison;
import okeanos.model.FfessmLicence;
import okeanos.model.Insurance;
import okeanos.model.Subscription;
import okeanos.util.AppProperties;

public class DashboardResource extends AbstractResource {

	protected String ressourcePath = AppProperties.API_CONTEXT + "/dashboard";

	public DashboardResource() {
		super.setupEndpoints();

		get(ressourcePath + "/saison/:saisonId/account/:account_id", (request, response) -> {
			setSecurity(request, response);
			if (!SecurityResource.isLogin(request)) {
				throw new IllegalAccessException("Illegal Access");
			}

			AdherentInfoSaison infoSaison = AdherentInfoSaisonDao
					.getItemById(AdherentInfoSaisonDao.getIdBySaisonAndAccount(new Long(request.params(":saisonId")),
							new Long(request.params(":account_id"))));

			return gson.toJson(new Adherent(infoSaison));
		});

		get(ressourcePath + "/saison/:saisonId", (request, response) -> {
			setSecurity(request, response);
			if (!SecurityResource.isAdmin(request)) {
				throw new IllegalAccessException("Illegal Access");
			}

			Long saisonId = new Long(request.params(":saisonId"));

			// Préparation des listes d'objet
			HashMap<Long, Account> accounts = new HashMap<Long, Account>();
			for (Account account : AccountDao.getAllItems(false)) {
				accounts.put(account.getId(), account);
			}

			HashMap<Long, AdherentInfo> adherentInfos = new HashMap<Long, AdherentInfo>();
			for (AdherentInfo adherentInfo : AdherentInfoDao.getAllItems()) {
				adherentInfos.put(adherentInfo.getFk_account_id(), adherentInfo);
			}

			HashMap<Long, Subscription> subscriptions = new HashMap<Long, Subscription>();
			for (Subscription sub : SubscriptionDao.getAllItemsForSaison(saisonId)) {
				subscriptions.put(sub.getId(), sub);
			}

			HashMap<Long, FfessmLicence> ffessmLicences = new HashMap<Long, FfessmLicence>();
			for (FfessmLicence licence : FfessmLicenceDao.getAllItemsForSaison(saisonId)) {
				ffessmLicences.put(licence.getId(), licence);
			}

			HashMap<Long, Insurance> insurances = new HashMap<Long, Insurance>();
			for (Insurance insurance : InsuranceDao.getAllItemsForSaison(saisonId)) {
				insurances.put(insurance.getId(), insurance);
			}

			// Construction de la liste des adhérents
			ArrayList<Adherent> res = new ArrayList<Adherent>();
			for (AdherentInfoSaison infoSaison : AdherentInfoSaisonDao.getAllItemsForSaison(saisonId)) {
				res.add(new Adherent(infoSaison, accounts, adherentInfos, insurances, ffessmLicences, subscriptions));
			}

			return gson.toJson(res);
		});

	}

	@SuppressWarnings("unused")
	private class Adherent {
		public final Account account;
		public final AdherentInfo info;
		public final AdherentInfoSaison infoSaison;
		public Insurance insurance;
		public FfessmLicence licence;
		public Subscription subscription;

		public Adherent(AdherentInfoSaison infoSaison) {
			this.infoSaison = infoSaison;
			this.account = AccountDao.getItemById(infoSaison.getFk_account_id(), false);
			this.info = AdherentInfoDao.getItemByAccountId(infoSaison.getFk_account_id());
			if (infoSaison.getFk_insurance_id() != null)
				this.insurance = InsuranceDao.getItemById(infoSaison.getFk_insurance_id());
			if (infoSaison.getFk_ffessm_licence_id() != null)
				this.licence = FfessmLicenceDao.getItemById(infoSaison.getFk_ffessm_licence_id());
			if (infoSaison.getFk_subscription_id() != null)
				this.subscription = SubscriptionDao.getItemById(infoSaison.getFk_subscription_id());
		}

		public Adherent(AdherentInfoSaison infoSaison, HashMap<Long, Account> accounts,
				HashMap<Long, AdherentInfo> adherentInfos, HashMap<Long, Insurance> insurances,
				HashMap<Long, FfessmLicence> ffessmLicences, HashMap<Long, Subscription> subscriptions) {

			this.infoSaison = infoSaison;
			this.account = accounts.get(infoSaison.getFk_account_id());
			this.info = adherentInfos.get(infoSaison.getFk_account_id());
			if (infoSaison.getFk_insurance_id() != null)
				this.insurance = insurances.get(infoSaison.getFk_insurance_id());
			if (infoSaison.getFk_ffessm_licence_id() != null)
				this.licence = ffessmLicences.get(infoSaison.getFk_ffessm_licence_id());
			if (infoSaison.getFk_subscription_id() != null)
				this.subscription = subscriptions.get(infoSaison.getFk_subscription_id());
		}

	}

}