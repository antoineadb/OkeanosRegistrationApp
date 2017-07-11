/* 
 * Contrôleur de la liste des saisons
 */
okeanosAppControllers.controller('saisonListCtrl', function ($scope, securityService, Saison) {
    securityService.checkIsLogin();

    $scope.adminMode = securityService.checkIsAdmin();
    $scope.saisonList = Saison.query();

    $scope.edit = function (saison) {
        $scope.modalId = saison.id;
        $scope.modalLabel = saison.label;
        $scope.modalStartDate = new Date(saison.start_date);
        $scope.modalEndDate = new Date(saison.end_date);
    };

    $scope.saveItem = function () {
        console.log('controle sécu : ' + securityService.checkIsAdmin());
        if (securityService.checkIsAdmin() == false) {
            console.log('Enregistrement non authorisé');
            return false;
        }
        console.log('Enregistrement d une saison : ' + $scope.modalLabel);
        var saison = new Saison();
        saison.id = $scope.modalId;
        saison.label = $scope.modalLabel;
        saison.start_date = new Date($scope.modalStartDate);
        saison.end_date = $scope.modalEndDate;
        saison.$save();
        console.log('Enregistrement terminé');

        $scope.refreshList();
    };

    $scope.remove = function (saison) {
        if (securityService.checkIsAdmin() == false) {
            console.log('Suppression non authorisé');
            return false;
        }
        console.log('Suppression de la saison : ' + saison.label);
        $scope.saisonList = [];
        Saison.delete(saison);

        $scope.refreshList();
    };

    $scope.refreshList = function () {
        $scope.saisonList = [];
        $scope.modalId = null;
        $scope.modalLabel = null;
        $scope.modalStartDate = null;
        $scope.modalEndDate = null;
        $scope.saisonList = Saison.query();
    };
});