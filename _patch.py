import sys

path = 'C:/Users/IrMine/Desktop/grworld/index.html'
with open(path, 'r', encoding='utf-8') as f:
    html = f.read()

# ---- 1. Заменить dev-notice на tabs + statsTable ----
# Находим блок по уникальному фрагменту
needle = 'padding:14px 18px;background:rgba(200,168,75,.06)'
idx = html.find(needle)
if idx == -1:
    print('FAIL: 1/3 dev-notice not found')
else:
    # Найти начало тега <div (ищем назад от idx)
    start = html.rfind('<div', 0, idx)
    # Найти закрывающий </div> после idx
    end = html.find('</div>', idx) + len('</div>')
    old_block = html[start:end]
    new_stats = """    <div style="margin-top:24px">
      <div class="section-label" style="margin-bottom:12px">Таблица лидеров</div>
      <div style="display:flex;gap:8px;margin-bottom:16px">
        <button id="tab-all" onclick="switchStatsTab('all')" style="padding:10px 24px;border-radius:8px;border:1px solid var(--border,#2a2a2a);background:var(--card,#1a1a1a);color:var(--text,#fff);cursor:pointer;font-size:16px;font-weight:600">Все игроки</button>
        <button id="tab-online" onclick="switchStatsTab('online')" style="padding:10px 24px;border-radius:8px;border:1px solid var(--border,#2a2a2a);background:transparent;color:var(--muted,#888);cursor:pointer;font-size:16px;font-weight:600">Онлайн</button>
      </div>
      <div id="statsTable"></div>
    </div>"""
    html = html[:start] + new_stats + html[end:]
    print('OK: 1/3 stats tabs inserted')

# ---- 2. Вставить JS перед </script> ----
js_code = r"""
// ---- STATS ----
let lastLeaders = [];
let currentStatsTab = 'all';

function switchStatsTab(tab) {
  currentStatsTab = tab;
  document.getElementById('tab-all').style.background = tab === 'all' ? 'var(--card,#1a1a1a)' : 'transparent';
  document.getElementById('tab-all').style.color = tab === 'all' ? 'var(--text,#fff)' : 'var(--muted,#888)';
  document.getElementById('tab-online').style.background = tab === 'online' ? 'var(--card,#1a1a1a)' : 'transparent';
  document.getElementById('tab-online').style.color = tab === 'online' ? 'var(--text,#fff)' : 'var(--muted,#888)';
  renderStatsTable(lastLeaders);
}

function renderStatsTable(leaders) {
  const tbl = document.getElementById('statsTable');
  if (!tbl) return;
  if (!leaders || leaders.length === 0) {
    tbl.innerHTML = '<div style="color:var(--muted)">Нет данных</div>';
    return;
  }
  const filtered = currentStatsTab === 'online' ? leaders.filter(p => p.is_online) : leaders;
  if (filtered.length === 0) {
    tbl.innerHTML = '<div style="color:var(--muted)">Никого нет онлайн ⛏️</div>';
    return;
  }
  const rows = filtered.map((p) => {
    const hours = Math.floor((p.total_seconds || 0) / 3600);
    const skinUrl = 'https://mc-heads.net/avatar/' + p.nickname + '/64';
    const isOnline = p.is_online ? '<span style="display:inline-block;width:8px;height:8px;background:#4caf50;border-radius:50%;margin-right:5px;vertical-align:middle"></span>' : '';
    return `<div style="display:flex;flex-direction:column;align-items:center;gap:10px;background:var(--card,#1a1a1a);border:1px solid var(--border,#2a2a2a);border-radius:10px;padding:16px 12px;width:160px;flex-shrink:0;transition:border-color .2s;cursor:default;" onmouseover="this.style.borderColor='#c8a84b'" onmouseout="this.style.borderColor='var(--border,#2a2a2a)'">
      <img src="${skinUrl}" alt="${p.nickname}" style="width:64px;height:64px;border-radius:6px;image-rendering:pixelated" onerror="this.src='https://mc-heads.net/avatar/Steve/64'">
      <div style="text-align:center">
        <div style="font-weight:600;font-size:14px">${isOnline}${p.nickname}</div>
        <div style="margin-top:6px;font-size:12px;color:var(--muted,#888)">Наиграл:</div>
        <div style="font-size:20px;font-weight:700;color:#c8a84b">${hours}ч</div>
      </div>
    </div>`;
  }).join('');
  tbl.innerHTML = '<div style="display:flex;flex-wrap:wrap;gap:12px;">' + rows + '</div>';
}

async function loadStats() {
  try {
    const res = await fetch('/proxy.php?path=/web/server-stats');
    if (res.ok) {
      const data = await res.json();
      ['stat-online','nav-online','hero-online'].forEach(id => {
        const el = document.getElementById(id);
        if (el) el.textContent = id === 'nav-online' ? data.online + ' онлайн' : data.online;
      });
      const tps = document.getElementById('stat-tps');
      if (tps) tps.textContent = data.tps;
    }
  } catch(e) { console.warn('server-stats error', e); }
  try {
    const res2 = await fetch('/proxy.php?path=/web/stats');
    if (res2.ok) {
      lastLeaders = await res2.json();
      renderStatsTable(lastLeaders);
    }
  } catch(e) { console.warn('stats error', e); }
}

let statsRefreshTimer = null;
function startStatsRefresh() {
  if (statsRefreshTimer) clearInterval(statsRefreshTimer);
  statsRefreshTimer = setInterval(loadStats, 30000);
}
function stopStatsRefresh() {
  if (statsRefreshTimer) { clearInterval(statsRefreshTimer); statsRefreshTimer = null; }
}

// ---- COMMUNITIES ----
async function loadCommunities() {
  try {
    const res = await fetch('/proxy.php?path=/web/communities');
    if (!res.ok) return;
    const list = await res.json();
    window._communities = list;
    const grid = document.getElementById('commGrid');
    if (!grid) return;
    if (!list || list.length === 0) {
      grid.innerHTML = '<div style="color:var(--muted)">Общин пока нет. Создай первую!</div>';
      return;
    }
    const user = JSON.parse(localStorage.getItem('discord_user') || 'null');
    grid.innerHTML = list.map(c => `
      <div class="comm-card">
        ${c.banner_url ? `<img src="${c.banner_url}" style="width:100%;height:80px;object-fit:cover;border-radius:6px 6px 0 0;margin-bottom:8px">` : ''}
        <div class="comm-top">
          <span class="comm-icon">${c.icon || '🏘️'}</span>
          <div>
            <div class="comm-name">${c.name}</div>
            <div class="comm-members">👥 ${c.member_count} участников</div>
          </div>
          ${user && user.id === c.owner_discord_id ? `<button onclick="openEditComm(${c.id})" style="margin-left:auto;background:none;border:none;cursor:pointer;font-size:18px">⚙️</button>` : ''}
        </div>
        <div class="comm-desc">${c.description || ''}</div>
        <div class="comm-footer">
          <span class="comm-tag">${c.tag || ''}</span>
          <div style="display:flex;gap:6px">
            ${c.discord_url ? `<a href="${c.discord_url}" target="_blank" class="btn btn-sm">Discord</a>` : ''}
            <button class="comm-join" onclick="joinCommunity(${c.id})">Вступить</button>
          </div>
        </div>
      </div>`).join('');
  } catch(e) { console.warn('communities error', e); }
}

async function joinCommunity(id) {
  const user = JSON.parse(localStorage.getItem('discord_user') || 'null');
  if (!user) { openModal('loginModal'); return; }
  const res = await fetch('/proxy.php?path=/web/communities/' + id + '/join', {
    method: 'POST',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify({ discord_id: user.id })
  });
  const data = await res.json();
  if (!res.ok) { alert(data.detail || 'Ошибка'); return; }
  loadCommunities();
}

let editCommId = null;
function openEditComm(id) {
  const c = (window._communities || []).find(x => x.id === id);
  editCommId = id;
  if (c) {
    document.getElementById('editCommName').value = c.name || '';
    document.getElementById('editCommIcon').value = c.icon || '';
    document.getElementById('editCommDesc').value = c.description || '';
    document.getElementById('editCommTag').value = c.tag || '';
    document.getElementById('editCommBanner').value = c.banner_url || '';
    document.getElementById('editCommDiscord').value = c.discord_url || '';
  }
  openModal('editCommModal');
}

async function saveCommEdit() {
  const user = JSON.parse(localStorage.getItem('discord_user') || 'null');
  if (!user || !editCommId) return;
  const body = {
    discord_id: user.id,
    name: document.getElementById('editCommName').value.trim(),
    icon: document.getElementById('editCommIcon').value.trim(),
    description: document.getElementById('editCommDesc').value.trim(),
    tag: document.getElementById('editCommTag').value.trim(),
    banner_url: document.getElementById('editCommBanner').value.trim(),
    discord_url: document.getElementById('editCommDiscord').value.trim(),
  };
  const res = await fetch('/proxy.php?path=/web/communities/' + editCommId, {
    method: 'PATCH',
    headers: {'Content-Type':'application/json'},
    body: JSON.stringify(body)
  });
  if (!res.ok) { const d = await res.json(); alert(d.detail || 'Ошибка'); return; }
  closeModal('editCommModal');
  loadCommunities();
}

// ---- НАВИГАЦИЯ ----
const _origShowPage = window.showPage;
window.showPage = function(name) {
  if (_origShowPage) _origShowPage(name);
  if (name === 'stats') { loadStats(); startStatsRefresh(); }
  else stopStatsRefresh();
  if (name === 'communities') loadCommunities();
};

loadStats();
loadCommunities();
"""

if '</script>' in html:
    html = html.replace('</script>', js_code + '\n</script>', 1)
    print('OK: 2/3 JS inserted')
else:
    print('FAIL: 2/3 no </script> found')

# ---- 3. Добавить editCommModal перед </body> ----
edit_modal = """
<div class="overlay" id="editCommModal">
  <div class="modal modal-wide">
    <h2>Настройка общины</h2>
    <div class="inp-group"><label>Название</label><input type="text" id="editCommName" maxlength="40"/></div>
    <div class="inp-group"><label>Иконка (эмодзи)</label><input type="text" id="editCommIcon" maxlength="2"/></div>
    <div class="inp-group"><label>Описание</label><textarea id="editCommDesc" rows="3"></textarea></div>
    <div class="inp-group"><label>Тематика</label><input type="text" id="editCommTag" placeholder="Строительство, Торговля, PvP..."/></div>
    <div class="inp-group"><label>Баннер (ссылка с planetminecraft.com/banner)</label><input type="text" id="editCommBanner" placeholder="https://..."/></div>
    <div class="inp-group"><label>Discord сервер</label><input type="text" id="editCommDiscord" placeholder="https://discord.gg/..."/></div>
    <button onclick="closeModal('editCommModal')">Отмена</button>
    <button class="btn btn-primary" onclick="saveCommEdit()">Сохранить</button>
  </div>
</div>
"""

if '</body>' in html:
    html = html.replace('</body>', edit_modal + '</body>', 1)
    print('OK: 3/3 editCommModal inserted')
else:
    print('FAIL: 3/3 no </body> found')

with open(path, 'w', encoding='utf-8') as f:
    f.write(html)
print('File saved successfully.')
