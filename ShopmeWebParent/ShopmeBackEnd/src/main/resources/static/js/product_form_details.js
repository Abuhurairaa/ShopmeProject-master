
$(document).ready(function() {
	
	$("a[name='linkRemoveDetail']").each(function(index){
		
		$(this).click(function(){
			removeDetailSectionByIndex(index);
		});
	});

});

function addNextDetailSection(){
	
	allDivDetails = $("[id^='divDetail']");
	divDetailsCount = allDivDetails.length;
	
	
	
	htmlDetailSection = `
		<div class="form-inline" id="divDetail${divDetailsCount}">
			<input type="hidden" name="detailIDs" value="0"/>
			<label class="m-3">Name:</label>
			<input type="text" maxlength="255" class="form-control w-25" name="detailNames"/>
			<label class="m-3">Value:</label>
			<input type="text" maxlength="255" class="form-control w-25" name="detailValues"/>
		</div>
	`;
	
	$("#divProductDetails").append(htmlDetailSection);
	
	previousDivDetailsSection = allDivDetails.last();
	previousDivDetailId = previousDivDetailsSection.attr("id");
	
	htmlLinkRemove = `
		<a class="btn fas fa-times-circle fa-2x" 
			href="javascript:removeDetailSectionById('${previousDivDetailId}')"
			title="Remove this detail"></a>
	`;
		
	previousDivDetailsSection.append(htmlLinkRemove);
	
	$("input[name='detailNames']").last().focus();
	
}

function removeDetailSectionById(id){
	$("#" + id).remove();
}

function removeDetailSectionByIndex(index){
	$("#divDetail" + index).remove();
}
