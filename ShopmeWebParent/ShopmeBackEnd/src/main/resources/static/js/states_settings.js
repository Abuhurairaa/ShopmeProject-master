var buttonLoadCountriesForStates;
var dropDownCountriesForStates;
var dropDownStates;
var buttonAddState;
var buttonUpdateState;
var buttonDeleteState;
var labelStateName;
var fieldStateName;


$(document).ready(function(){
	buttonLoadCountriesForStates = $("#buttonLoadCountriesForStates");
	dropDownCountriesForStates = $("#dropDownCountriesForStates");
	dropDownStates = $("#dropDownStates")
	buttonAddState = $("#buttonAddState");
	buttonUpdateState = $("#buttonUpdateState");
	buttonDeleteState = $("#buttonDeleteState");
	labelStateName = $("#labelStateName");
	fieldStateName = $("#fieldStateName");
	
	buttonLoadCountriesForStates.click(function(){
		loadCountries4States();
	});
	
	dropDownCountriesForStates.on("change", function(){
		loadStates4Country();
	});
	
	dropDownStates.on("change", function(){
		changeFormStateToSelectdState();
	});
	
	buttonAddState.click(function(){
		if(buttonAddState.val() == "Add"){
			addState();
		}else{
			changeFormStateToNew();
		}
		
	});
	
	buttonUpdateState.click(function(){
		updateState();
	});
	
	buttonDeleteState.click(function(){
		deleteState();
	});
	
});

function deleteState(){
	stateId = dropDownStates.val();
	
	url = contextPath + "states/delete/" + stateId;
	
	$.ajax({
		type: 'DELETE',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		}
	}).done(function(){
		$("#dropDownStates option[value='" + stateId + "']").remove();
		changeFormStateToNew();
		showToastMessage("The State has been deleted.")
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!")
	});
}

function updateState(){
	if(!validateformState()) return;
	
	url = contextPath + "states/save";
	stateId = dropDownStates.val();
	stateName = fieldStateName.val();
	
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();
	
	jsonData = {name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId){
		$("#dropDownStates option:selected").text(stateName);
		
		showToastMessage("The State has been Updated");
		
		changeFormStateToNew();
		
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!")
	});
}

function addState(){
	if(!validateformState()) return;
	
	url = contextPath + "states/save";
	stateName = fieldStateName.val();
	
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	countryName = selectedCountry.text();
	
	jsonData = {name: stateName, country: {id: countryId, name: countryName}};
	
	$.ajax({
		type: 'POST',
		url: url,
		beforeSend: function(xhr){
			xhr.setRequestHeader(csrfHeaderName, csrfValue);
		},
		data: JSON.stringify(jsonData),
		contentType: 'application/json'
	}).done(function(stateId){
		selectNewlyAddedState(stateId, stateName);
		showToastMessage("The new State has been added");
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!");
	});
}

function validateformState(){
	formState = document.getElementById("formState");
	if(!formState.checkValidity()){
		formState.reportValidity();
		return false;
	}
	
	return true;
}

function selectNewlyAddedState(stateId, stateName){
	$("<option>").val(stateId).text(stateName).appendTo(dropDownStates);
	
	$("#dropDownStates option[value='" + stateId + "']").prop("selected", true);
	
	fieldStateName.val("").focus();
}

function changeFormStateToNew(){
	buttonAddState.val("Add");
	labelStateName.text("State/Province Name:");
	
	buttonUpdateState.prop("disabled", true);
	buttonDeleteState.prop("disabled", true);
	
	fieldStateName.val("").focus;
}

function changeFormStateToSelectdState(){
	buttonAddState.prop("value", "New");
	buttonUpdateState.prop("disabled", false);
	buttonDeleteState.prop("disabled", false);
	
	labelStateName.text("Selected State/Province:");
	
	selectedStateName = $("#dropDownStates option:selected").text();
	fieldStateName.val(selectedStateName);
	
}

function loadStates4Country(){
	selectedCountry = $("#dropDownCountriesForStates option:selected");
	countryId = selectedCountry.val();
	url = contextPath + "states/list_by_country/" + countryId;
	
	$.get(url, function(responseJSON){
		dropDownStates.empty();
		
		$.each(responseJSON, function(index, state){
			$("<option>").val(state.id).text(state.name).appendTo(dropDownStates);
		});
	}).done(function(){
		changeFormStateToNew();
		showToastMessage("All States has been load for Country." + selectedCountry.text());
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!");
	});
}

function loadCountries4States(){
	url = contextPath + "countries/list";
	
	$.get(url, function(responseJSON){
		dropDownCountriesForStates.empty();
		
		$.each(responseJSON, function(index, country){
			$("<option>").val(country.id).text(country.name).appendTo(dropDownCountriesForStates);
		});
	}).done(function(){
		buttonLoadCountriesForStates.val("Refresh Country List");
		showToastMessage("All countries has been loaded.");
	}).fail(function(){
		showToastMessage("Error: Could not connect to the server or server encounter an error!");
	});
}

function showToastMessage(message){
	$("#toastMessage").text(message);
	$(".toast").toast("show");
}
