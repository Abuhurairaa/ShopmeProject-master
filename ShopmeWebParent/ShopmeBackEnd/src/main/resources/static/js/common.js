$(document).ready(function(){
	$("#logoutLink").on("click", function(e){
		e.preventDefault();
		document.logoutForm.submit();
	});
	
	customizeDropDownMenu();
	customiseTabs();
	
});

function customizeDropDownMenu(){
	
	$(".navbar .dropdown").hover(
		function(){
			$(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
		},
		function(){
			$(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
		}
	);
	
	$(".dropdown > a").click(function(){
		location.href = this.href;
	});
}

function customiseTabs(){
	//javascript to enable link tab
	var url = document.location.toString();
	
	if(url.match('#')){
		$('.nav-tabs a[href="#'+url.split('#')[1]+ '"]').tab('show');
	}
	
	//change hash for page-reload
	$('.nav-tabs a').on('shown.bs.tabs', function(e){
		window.location.hash = e.targert.hash;
	});
	
}
