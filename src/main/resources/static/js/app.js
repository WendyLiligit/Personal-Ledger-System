// 全局变量
let currentPage = 1;
let pageSize = 10;
let totalPages = 1;
let currentFilters = { startDate: '', endDate: '', category: '', type: '', minAmount: '', maxAmount: '' };

// 加载记录列表
function loadRecords() {
    const params = new URLSearchParams({
        page: currentPage,
        size: pageSize,
        startDate: document.getElementById('startDate').value,
        endDate: document.getElementById('endDate').value,
        category: document.getElementById('categoryFilter').value,
        type: document.getElementById('typeFilter').value,
        minAmount: document.getElementById('minAmount').value,
        maxAmount: document.getElementById('maxAmount').value
    });
    fetch(`/api/records?${params}`)
        .then(res => res.json())
        .then(data => {
            renderRecords(data.content);
            totalPages = data.totalPages;
            renderPagination();
        })
        .catch(err => {
            console.error(err);
            document.getElementById('records-tbody').innerHTML = '<tr><td colspan="6">加载失败</td></tr>';
        });
}

function renderRecords(records) {
    const tbody = document.getElementById('records-tbody');
    if (!records.length) {
        tbody.innerHTML = '<tr><td colspan="6">暂无数据</td></tr>';
        return;
    }
    let html = '';
    records.forEach(r => {
        html += `<tr>
            <td>${r.date}</td>
            <td>${r.category}</td>
            <td>${r.note || ''}</td>
            <td>${r.type === 'income' ? '收入' : '支出'}</td>
            <td class="${r.type === 'income' ? 'text-success' : 'text-danger'}">${Number(r.amount).toFixed(2)}</td>
            <td>
                <a href="/records/edit/${r.id}" class="btn btn-sm btn-warning">编辑</a>
                <button class="btn btn-sm btn-danger" onclick="deleteRecord(${r.id})">删除</button>
            </td>
        </tr>`;
    });
    tbody.innerHTML = html;
}

function renderPagination() {
    const info = document.getElementById('page-info');
    info.innerText = `第 ${currentPage} / ${totalPages} 页`;
    const btns = document.getElementById('pagination-btns');
    btns.innerHTML = '';
    if (currentPage > 1) {
        const prev = document.createElement('button');
        prev.innerText = '上一页';
        prev.className = 'btn btn-default';
        prev.onclick = () => { currentPage--; loadRecords(); };
        btns.appendChild(prev);
    }
    if (currentPage < totalPages) {
        const next = document.createElement('button');
        next.innerText = '下一页';
        next.className = 'btn btn-default';
        next.onclick = () => { currentPage++; loadRecords(); };
        btns.appendChild(next);
    }
}

function loadSummary() {
    fetch('/api/records/summary')
        .then(res => res.json())
        .then(data => {
            document.getElementById('total-income').innerText = data.income.toFixed(2);
            document.getElementById('total-expense').innerText = data.expense.toFixed(2);
            document.getElementById('total-balance').innerText = (data.income - data.expense).toFixed(2);
        })
        .catch(err => console.error(err));
}

function loadCategories() {
    fetch('/api/categories')
        .then(res => res.json())
        .then(cats => {
            const select = document.getElementById('categoryFilter');
            let opts = '<option value="">全部分类</option>';
            cats.forEach(c => { opts += `<option value="${c.name}">${c.name}</option>`; });
            select.innerHTML = opts;
        })
        .catch(err => console.error(err));
}

function resetFilter() {
    document.getElementById('startDate').value = '';
    document.getElementById('endDate').value = '';
    document.getElementById('categoryFilter').value = '';
    document.getElementById('typeFilter').value = '';
    document.getElementById('minAmount').value = '';
    document.getElementById('maxAmount').value = '';
    currentPage = 1;
    loadRecords();
    loadSummary();
}

function deleteRecord(id) {
    if (confirm('确定删除该记录吗？')) {
        fetch(`/api/records/${id}`, { method: 'DELETE' })
            .then(res => {
                if (res.ok) {
                    loadRecords();
                    loadSummary();
                } else {
                    alert('删除失败');
                }
            })
            .catch(err => alert('删除失败'));
    }
}

// 手动记账页的保存函数（若需要）
function saveRecord() {
    // 略，由表单直接提交
}

// 退出登录
function confirmLogout() {
    if (confirm('确定退出登录吗？')) {
        window.location.href = '/logout';
    }
}