let current = new Date();

async function load() {

    const year = current.getFullYear();
    const month = current.getMonth() + 1;

    document.getElementById("month-label").textContent =
        year + "年 " + month + "月";

    const res = await fetch(`/shift/api/month?year=${year}&month=${month}`);
    const data = await res.json();

    renderTable(data.employees, year, month);
}

function renderTable(employees, year, month) {

    const table = document.getElementById("shift-table");
    table.innerHTML = "";

    const days = new Date(year, month, 0).getDate();

    const header = document.createElement("tr");
    header.innerHTML = "<th>社員</th>";

    for (let d = 1; d <= days; d++) {
        header.innerHTML += `<th>${d}</th>`;
    }

    table.appendChild(header);

    employees.forEach(emp => {

        const tr = document.createElement("tr");
        tr.innerHTML = `<td>${emp.fullName}</td>`;

        for (let d = 1; d <= days; d++) {

            const dateStr = `${year}-${String(month).padStart(2,'0')}-${String(d).padStart(2,'0')}`;
            const td = document.createElement("td");

            const type = emp.shifts[dateStr];

            if (type === "HOLIDAY") td.className = "holiday";
            if (type === "PAID") td.className = "paid";

            td.onclick = () => change(emp.employeeId, dateStr, type, td);

            tr.appendChild(td);
        }

        table.appendChild(tr);
    });
}

function nextType(type) {
    if (!type) return "HOLIDAY";
    if (type === "HOLIDAY") return "PAID";
    return "";
}

async function change(empId, date, currentType, td) {

    const newType = nextType(currentType);

    await fetch("/shift/api/change", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
            employeeId: empId,
            date: date,
            shiftType: newType
        })
    });

    load();
}

function prevMonth() {
    current.setMonth(current.getMonth() - 1);
    load();
}

function nextMonth() {
    current.setMonth(current.getMonth() + 1);
    load();
}

load();