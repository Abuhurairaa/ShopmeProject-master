var buttonLoad;
var dropCountry;
var buttonAddCountry;
var buttonUpdateCountry;
var buttonDeleteCountry;
var labelCountryName;
var fieldCountryName;
var fieldCountryCode;

$(document).ready(function(){
	buttonLoad = $("#buttonLoadCountries");
	dropCountry = $("#dropDownCountries");
	buttonAddCountry = $("#buttonAddCountry");
	buttonUpdateCountry = $("#buttonUpdateCountry");
	buttonDeleteCountry = $("#buttonDeleteCountry");
	labelCountryName = $("#labelCountryName");
	fieldCountryName = $("#fieldCountryName");
	fieldCountryCode = $("#fieldCountryCode");
	
	buttonLoad.click(function(){
		loadCountries();
	});
	
	dropCountry.on("change", function(){
		changeFormStateToSelectdCountry();
	});
	
	buttonAddCountry.click(function(){
		if(buttonAddCountry.val() == "Add"){
			addCountry();
		}else{
			changeFormStateToNewCountry();
		}
		
	});
	
	buttonUpdateCountry.click(function(){
		updateCountry();
	});
	
	buttonDeleteCountry.click(function(){
		deleteCountry();
	});
	
});

function deleteCountry(){
	optionValue = dropCountry.val();
	countryId = optionValue.split("-")[0];
	
	url = contextPath + "countries/delete/" + countryId;
	
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
		
	}).done(function(){
		$("#dropDownCountries option[value='" + optionValue + "']").remove();
		changeFormStateToNewCountry();
		showToastMessage("The country has been deleted.")
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!")
	});
}

function updateCountry(){
	
	if(!validateFormCountry()) return;
	
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	
	countryId = dropCountry.val().split("-")[0];
	
	jsonData = {id: countryId, name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId){
		$("#dropDownCountries option:selected").val(countryId + "-" + countryCode);
		$("#dropDownCountries option:selected").text(countryName);
		
		showToastMessage("The Country has been Updated");
		
		changeFormStateToNewCountry();
		
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!")
	});
}

function validateFormCountry(){
	formCountry = document.getElementById("formCountry");
	if(!formCountry.checkValidity()){
		formCountry.reportValidity();
		return false;
	}
	
	return true;
}

function addCountry(){
	
	if(!validateFormCountry()) return;
	
	url = contextPath + "countries/save";
	countryName = fieldCountryName.val();
	countryCode = fieldCountryCode.val();
	
	jsonData = {name: countryName, code: countryCode};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(countryId){
		selectNewlyAddedCountry(countryId, countryCode, countryName);
		showToastMessage("The new Country has been added");
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!")
	});
}

function selectNewlyAddedCountry(countryId, countryCode, countryName){
	optionValue = countryId + "-" + countryCode;
	$("<option>").val(optionValue).text(countryName).appendTo(dropCountry);
	
	$("#dropDownCountries option[value='" + optionValue + "']").prop("selected", true);
	
	fieldCountryCode.val("");
	fieldCountryName.val("").focus();
}

function changeFormStateToNewCountry(){
	buttonAddCountry.val("Add");
	labelCountryName.text("Country Name:");
	
	buttonUpdateCountry.prop("disabled", true);
	buttonDeleteCountry.prop("disabled", true);
	
	fieldCountryCode.val("");
	fieldCountryName.val("").focus;
}

function changeFormStateToSelectdCountry(){
	buttonAddCountry.prop("value", "New");
	buttonUpdateCountry.prop("disabled", false);
	buttonDeleteCountry.prop("disabled", false);
	
	labelCountryName.text("Selected Country:")
	
	selectedCountryName = $("#dropDownCountries option:selected").text();
	fieldCountryName.val(selectedCountryName);
	
	countryCode = dropCountry.val().split("-")[1];
	fieldCountryCode.val(countryCode);
}

function loadCountries(){
	url = contextPath + "countries/list";
	$.get(url, function(responseJSON){
		dropCountry.empty();
		
		$.each(responseJSON, function(index, country){
			optionValue = country.id + "-" + country.code;
			$("<option>").val(optionValue).text(country.name).appendTo(dropCountry);
		});
	}).done(function(){
		buttonLoad.val("Refresh Country List");
		showToastMessage("All countries has been loaded.")
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!")
	});
}

function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast("show");
}
