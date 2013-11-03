'use strict';
/* http://docs-next.angularjs.org/api/angular.module.ng.$compileProvider.directive */

angular.module('travel_agency.directives', []).directive('appVersion',
		[ 'version', function(version) {
			return function(scope, elm, attrs) {
				elm.text(version);
			};
		} ]);

angular.module('travel_agency.directives', []).directive('pwCheck', [ function() {
	return {
		require : 'ngModel',
		link : function(scope, elem, attrs, ctrl) {
			var firstPassword = '#' + attrs.pwCheck;
			elem.add(firstPassword).on('keyup', function() {
				scope.$apply(function() {
					var v = elem.val() === $(firstPassword).val();
					ctrl.$setValidity('pwmatch', v);
				});
			});
		}
	}
} ]);