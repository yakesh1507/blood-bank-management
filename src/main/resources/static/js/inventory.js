function filterInventory() {

    const bloodGroup = document.getElementById("bloodGroupFilter").value;
    const expiryStatus = document.getElementById("expiryFilter").value;

    const rows = document.querySelectorAll(".inventory-table tbody tr");

    rows.forEach(row => {

        const groupCell = row.cells[1];
        const statusCell = row.cells[5];

        if (!groupCell || !statusCell) {
            return;
        }

        const group = groupCell.textContent.trim();
        const statusElement = statusCell.querySelector(".expiry-badge");

        const status = statusElement
            ? statusElement.className.split(" ").find(className =>
                ["valid", "expiring-soon", "expired"].includes(className)
            )
            : "";

        const groupMatches = bloodGroup === "" || group === bloodGroup;
        const statusMatches = expiryStatus === "" || status === expiryStatus;

        row.style.display = groupMatches && statusMatches ? "" : "none";
    });
}

document.addEventListener("DOMContentLoaded", function () {

    document.getElementById("bloodGroupFilter")
        .addEventListener("change", filterInventory);

    document.getElementById("expiryFilter")
        .addEventListener("change", filterInventory);

});