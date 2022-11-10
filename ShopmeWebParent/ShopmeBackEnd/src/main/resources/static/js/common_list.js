function clearFilter() {
	window.location = moduleURL;
}

function showDeleteConfirmModal(link, entityName) {
	/*link = $(this);*/
	entityId = link.attr("entityId");
	$("#yesButton").attr("href", link.attr("href"));
	$("#confirmText").text("Are you sure want to delete this "
		+ entityName + " Id: " + entityId + " ?");
	$("#confirmModal").modal();
}