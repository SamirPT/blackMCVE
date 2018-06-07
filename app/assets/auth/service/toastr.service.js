(function () {
    'use strict';

    angular.module('mrblack.auth').factory('Toastr', function Toastr() {

        var options = {
            "closeButton": true,
            "debug": false,
            "progressBar": false,
            "positionClass": "toast-top-right",
            "onclick": null,
            "showDuration": "400",
            "hideDuration": "1000",
            "timeOut": "1600",
            "extendedTimeOut": "1000",
            "showEasing": "swing",
            "hideEasing": "linear",
            "showMethod": "fadeIn",
            "hideMethod": "fadeOut"
        };

        toastr.options = options;

        return {
            success: function(header, message) {
                toastr.success(header, message);
            },

            error: function(header, message) {
                toastr.error(header, message, {'timeOut': 3000});
            }
        };
    });
}());
