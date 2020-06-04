const MIN_PASSWORD_STRENGTH_PERCENTAGE = 75;
const MIN_PASSWORD_LENGTH = 8;

(function($) {
	$.fn.alterClass = function(removals, additions) {
		var self = this;
	
		if (removals.indexOf('*') === -1) {
			// Use native jQuery methods if there is no wildcard matching
			self.removeClass(removals);
			return !additions ? self : self.addClass(additions);
		}

		var patt = new RegExp('\\s' + 
				removals.
					replace( /\*/g, '[A-Za-z0-9-_]+' ).
					split( ' ' ).
					join( '\\s|\\s' ) + 
				'\\s', 'g');

		self.each(function(i, it) {
			var cn = ' ' + it.className + ' ';
			while (patt.test(cn)) {
				cn = cn.replace(patt, ' ');
			}
			it.className = $.trim(cn);
		});

		return !additions ? self : self.addClass(additions);
	};
})( jQuery );

function cleanPasswordStrengthCheckerBar() {
	$('.password-input-meter .password-input-dot').alterClass('password-input-dot-selected-*', '');
}

function generatePasswordStrengthCheckerBar(password) {
	var score = checkPercentage(password);
	var start = countStart(score);
	var colorClass = determineColorClass(score, password);

	$('.password-input-meter .password-input-dot:nth-child(n+'+start+'):nth-child(-n+4)').addClass('password-input-dot-selected-'+colorClass);
	buttonControlCentre(colorClass);
}

function determineColorClass(score, password) {
	var colorClass = "";

	if (score > 0 && score <= 50) {
		colorClass = "worse";
	} else if (score > 50) {
		if (password.length < 8) {
			colorClass = "bad";
		} else {
			colorClass = "good";
		}
	} else {
		colorClass = "neutral";
	}

	return colorClass;
}

function countStart(score) {
	var start = 0;

	if (score > 0 && score <= 25) {
		start = 4; 
	} else if (score > 25 && score <= 50) {
		start = 3;
	} else if (score > 50 && score <= 75) {
		start = 2;
	} else if (score > 75){
		start = 1;
	}

	return start;
}

function checkPercentage(password) {
	var strengthPercentage = 0;

	// this partial regex from bobbi's code in org.gvm.product.gvm_poin.module.consumer.ConsumerPasswordStrengthChecker
	var partialRegexChecks = [
				".*[a-z]+.*", // lower
				".*[A-Z]+.*", // upper
				".*[\\d]+.*", // digits
				".*[`~@#%&:;\"',/\\<\\(\\[\\{\\^\\-\\=\\$\\!\\|\\]\\}\\)\\?\\*\\+\\.\\>]+.*" // symbols
		];

	for (i = 0, text = ""; i < partialRegexChecks.length; i++) {
    	var pattern = new RegExp(partialRegexChecks[i]);
    	if (pattern.test(password) == true) {
			strengthPercentage += 25;
		}
	}

	return parseInt(strengthPercentage);
}

function buttonControlCentre(colorClass="neutral") {
	if (colorClass == "good") {
		$('.btn-submit').prop('disabled', false);
	} else {
		$('.btn-submit').prop('disabled', true);
	}
}

$(function(){
	buttonControlCentre();
	$('#password').on('input', function() {
    	var password = $(this).val();  
				
		cleanPasswordStrengthCheckerBar();
		generatePasswordStrengthCheckerBar(password);		
	});
});