"""
Полная реконструкция index.html: берём index_modified.html как базу,
применяем все изменения сессии + общины, загружаем на Beget.
"""
import paramiko, sys

BT = chr(96)  # backtick для JS template literals

# ── Загрузка базового файла ───────────────────────────────────────────────
with open("C:/Users/IrMine/Desktop/grworld/index_modified.html", "rb") as f:
    html = f.read().decode("utf-8")

log = []

def apply(label, old, new):
    global html
    if old in html:
        html = html.replace(old, new, 1)
        log.append(f"OK : {label}")
    elif new in html:
        log.append(f"SKP: {label} (already applied)")
    else:
        log.append(f"ERR: {label}")

# ════════════════════════════════════════════════════════════════════════════
# А. discordLogin() → /cabinet.html
# ════════════════════════════════════════════════════════════════════════════
apply("discordLogin -> /cabinet.html",
    "  function discordLogin() {\n"
    "    window.location.href = 'https://api.gryazworld.ru/auth/discord';\n"
    "  }",
    "  function discordLogin() {\n"
    "    window.location.href = '/cabinet.html';\n"
    "  }"
)

# ════════════════════════════════════════════════════════════════════════════
# Б. Найти и прочитать logout() — нужно знать точный текст
# ════════════════════════════════════════════════════════════════════════════
# Находим logout по наличию api.gryazworld и заменяем целиком
import re
logout_match = re.search(r'(  async function logout\(\) \{.*?\n  \})', html, re.DOTALL)
if logout_match and 'api.gryazworld.ru' in logout_match.group(1):
    old_logout = logout_match.group(1)
    new_logout = (
        "  function logout() {\n"
        "    localStorage.removeItem('discord_user');\n"
        "    localStorage.removeItem('discord_token');\n"
        "    document.getElementById('user-info').style.display = 'none';\n"
        "    document.getElementById('btn-login').style.display = '';\n"
        "    setBuyButtons(false);\n"
        "    window.location.reload();\n"
        "  }"
    )
    html = html.replace(old_logout, new_logout, 1)
    log.append("OK : logout() -> localStorage version")
else:
    log.append("SKP: logout() (already updated or not found)")

# ════════════════════════════════════════════════════════════════════════════
# В. btn-login onclick → /cabinet.html
# ════════════════════════════════════════════════════════════════════════════
apply("btn-login onclick -> /cabinet.html",
    "onclick=\"window.location.href='https://api.gryazworld.ru/auth/discord'\">",
    "onclick=\"window.location.href='/cabinet.html'\">"
)

# ════════════════════════════════════════════════════════════════════════════
# Г. playersList → statsTable + вкладки
# ════════════════════════════════════════════════════════════════════════════
# Убрать старый блок playersList и заменить на вкладки + statsTable
old_players_block = re.search(
    r'    <div style="margin-bottom:16px">\s*'
    r'<div class="section-label"[^>]*>Игроки в сети</div>\s*'
    r'<div id="playersList"[^<]*</div>\s*</div>\s*</div>\n',
    html
)
# Попробуем простую замену
OLD_PLAYERS = (
    '    <div style="margin-bottom:16px">\n'
    '      <div class="section-label" style="margin-bottom:12px">Игроки в сети</div>\n'
    '      <div id="playersList">'
    '<div class="no-players">Сейчас никого нет онлайн. Станьте первым! ⛏️</div></div>\n'
    '    </div>'
)
NEW_TABS_AND_TABLE = (
    '    <div style="display:flex;gap:8px;margin-bottom:20px">\n'
    '      <button id="tab-all" onclick="switchStatsTab(\'all\')" '
    'style="padding:10px 24px;border-radius:8px;border:1px solid var(--border,#2a2a2a);'
    'background:var(--card,#1a1a1a);color:var(--text,#fff);cursor:pointer;font-size:16px;font-weight:600">'
    'Все игроки</button>\n'
    '      <button id="tab-online" onclick="switchStatsTab(\'online\')" '
    'style="padding:10px 24px;border-radius:8px;border:1px solid var(--border,#2a2a2a);'
    'background:transparent;color:var(--muted,#888);cursor:pointer;font-size:16px;font-weight:600">'
    'Онлайн</button>\n'
    '    </div>\n'
    '    <div id="statsTable" style="margin-top:8px">'
    '<div style="color:var(--muted);font-size:13px">Загрузка...</div></div>'
)
apply("playersList -> tabs + statsTable", OLD_PLAYERS, NEW_TABS_AND_TABLE)

# ════════════════════════════════════════════════════════════════════════════
# Д. Удалить "Ванильный выживательный сервер · Java Edition"
# ════════════════════════════════════════════════════════════════════════════
apply("Remove subtitle",
    "        <p>Ванильный выживательный сервер · Java Edition</p>\n",
    ""
)

# ════════════════════════════════════════════════════════════════════════════
# Е. Удалить stats-grid (4 плитки)
# ════════════════════════════════════════════════════════════════════════════
OLD_SGRID = (
    '\n'
    '    <div class="stats-grid">\n'
    '      <div class="sstat"><div class="sstat-v online" id="stat-online">0</div>'
    '<div class="sstat-l">Игроков онлайн</div></div>\n'
    '      <div class="sstat"><div class="sstat-v" id="stat-max">20</div>'
    '<div class="sstat-l">Максимум слотов</div></div>\n'
    '      <div class="sstat"><div class="sstat-v" id="stat-tps">20.0</div>'
    '<div class="sstat-l">TPS сервера</div></div>\n'
    '      <div class="sstat"><div class="sstat-v">Java</div>'
    '<div class="sstat-l">Версия</div></div>\n'
    '    </div>\n'
)
apply("Remove stats-grid", OLD_SGRID, '\n')

# ════════════════════════════════════════════════════════════════════════════
# Ж. showPage() — добавить вызовы loadStats + loadCommunities
# ════════════════════════════════════════════════════════════════════════════
OLD_SHOWPAGE_CLOSE = (
    "    if (name === 'home') document.getElementById('nav-home').classList.add('active');\n"
    "  }"
)
NEW_SHOWPAGE_CLOSE = (
    "    if (name === 'home') document.getElementById('nav-home').classList.add('active');\n"
    "    if (name === 'stats') { loadStats(); startStatsRefresh(); } else { stopStatsRefresh(); }\n"
    "    if (name === 'communities') loadCommunities();\n"
    "  }"
)
apply("showPage() + loadStats/loadCommunities", OLD_SHOWPAGE_CLOSE, NEW_SHOWPAGE_CLOSE)

# ════════════════════════════════════════════════════════════════════════════
# З. Заменить placeholder онлайн на loadStats()
# ════════════════════════════════════════════════════════════════════════════
OLD_PH = (
    "  // placeholder онлайн\n"
    "  document.getElementById('nav-online').textContent = '0 онлайн';\n"
    "  document.getElementById('hero-online').textContent = '0';\n"
    "  document.getElementById('stat-online').textContent = '0';"
)
apply("placeholder -> loadStats()", OLD_PH, "  loadStats();")

# ════════════════════════════════════════════════════════════════════════════
# И. Удалить communities[] array
# ════════════════════════════════════════════════════════════════════════════
OLD_ARRAY = (
    "  const communities = [\n"
    "    { name:'Северный форт', icon:'🏰', members:4, desc:'Строим укреплённое поселение на севере карты. Ищем шахтёров и строителей.', tag:'Строительство' },\n"
    "    { name:'Торговая гильдия', icon:'⚗️', members:7, desc:'Объединение торговцев. Рынки, аукционы, торговые маршруты между общинами.', tag:'Торговля' },\n"
    "    { name:'Деревня Гряз', icon:'🌾', members:12, desc:'Первая и крупнейшая деревня. Открыты для всех новичков.', tag:'Открытая' },\n"
    "  ];\n"
)
apply("Remove communities array", OLD_ARRAY, "")

# ════════════════════════════════════════════════════════════════════════════
# К. openCreateComm() → с проверкой авторизации
# ════════════════════════════════════════════════════════════════════════════
apply("openCreateComm() with auth check",
    "  function openCreateComm() { openModal('createCommModal'); }",
    "  function openCreateComm() {\n"
    "    const user = JSON.parse(localStorage.getItem('discord_user') || 'null');\n"
    "    if (!user) { openModal('loginModal'); return; }\n"
    "    openModal('createCommModal');\n"
    "  }"
)

# ════════════════════════════════════════════════════════════════════════════
# Л. createCommunity() → async API
# ════════════════════════════════════════════════════════════════════════════
OLD_CREATE = re.search(r'  function createCommunity\(\) \{.*?\n  \}', html, re.DOTALL)
if OLD_CREATE and 'communities.push' in OLD_CREATE.group(0):
    html = html.replace(OLD_CREATE.group(0),
        "  async function createCommunity() {\n"
        "    const user = JSON.parse(localStorage.getItem('discord_user') || 'null');\n"
        "    if (!user) { openModal('loginModal'); return; }\n"
        "    const name = document.getElementById('commName').value.trim();\n"
        "    const icon = document.getElementById('commIcon').value.trim() || '🏠';\n"
        "    const desc = document.getElementById('commDesc').value.trim();\n"
        "    const tag  = document.getElementById('commTag').value.trim() || 'Общая';\n"
        "    if (!name) { alert('Введите название!'); return; }\n"
        "    if (!desc) { alert('Добавьте описание!'); return; }\n"
        "    try {\n"
        "      const res = await fetch('/proxy.php?path=/web/communities', {\n"
        "        method: 'POST',\n"
        "        headers: { 'Content-Type': 'application/json' },\n"
        "        body: JSON.stringify({ name, icon, description: desc, tag, discord_id: user.id }),\n"
        "      });\n"
        "      const data = await res.json();\n"
        "      if (!res.ok) { alert(data.detail || 'Ошибка создания общины'); return; }\n"
        "      closeModal('createCommModal');\n"
        "      ['commName','commIcon','commDesc','commTag'].forEach(id => document.getElementById(id).value = '');\n"
        "      await loadCommunities();\n"
        "    } catch(e) { alert('Ошибка сети: ' + e.message); }\n"
        "  }"
    , 1)
    log.append("OK : createCommunity() -> async API")
else:
    log.append("SKP: createCommunity() (already updated or not found)")

# ════════════════════════════════════════════════════════════════════════════
# М. Удалить renderComm() + вызов renderComm()
# ════════════════════════════════════════════════════════════════════════════
old_render = re.search(r'  function renderComm\(\) \{.*?\n  \}\n  renderComm\(\);\n', html, re.DOTALL)
if old_render:
    html = html.replace(old_render.group(0), "", 1)
    log.append("OK : renderComm() + call removed")
else:
    log.append("SKP: renderComm() (not found or already removed)")

# ════════════════════════════════════════════════════════════════════════════
# Н. Добавить все новые JS функции перед </script>
# ════════════════════════════════════════════════════════════════════════════
NEW_JS = (
    "\n"
    "  // ── Server Stats & Leaderboard ──────────────────────────────────────────\n"
    "  let statsRefreshTimer = null;\n"
    "  let lastLeaders = [];\n"
    "  let currentStatsTab = 'all';\n"
    "\n"
    "  function switchStatsTab(tab) {\n"
    "    currentStatsTab = tab;\n"
    "    const btnAll    = document.getElementById('tab-all');\n"
    "    const btnOnline = document.getElementById('tab-online');\n"
    "    btnAll.style.background    = tab === 'all'    ? 'var(--card,#1a1a1a)' : 'transparent';\n"
    "    btnAll.style.color         = tab === 'all'    ? 'var(--text,#fff)'    : 'var(--muted,#888)';\n"
    "    btnOnline.style.background = tab === 'online' ? 'var(--card,#1a1a1a)' : 'transparent';\n"
    "    btnOnline.style.color      = tab === 'online' ? 'var(--text,#fff)'    : 'var(--muted,#888)';\n"
    "    renderStatsTable(lastLeaders);\n"
    "  }\n"
    "\n"
    "  function renderStatsTable(leaders) {\n"
    "    const tbl = document.getElementById('statsTable');\n"
    "    if (!leaders || leaders.length === 0) {\n"
    "      tbl.innerHTML = '<div style=\"color:var(--muted)\">Нет данных</div>';\n"
    "      return;\n"
    "    }\n"
    "    const filtered = currentStatsTab === 'online' ? leaders.filter(p => p.is_online) : leaders;\n"
    "    if (filtered.length === 0) {\n"
    "      tbl.innerHTML = '<div style=\"color:var(--muted)\">Никого нет онлайн \u26cf\ufe0f</div>';\n"
    "      return;\n"
    "    }\n"
    "    const rows = filtered.map((p, i) => {\n"
    "      const hours = Math.floor(p.total_seconds / 3600);\n"
    "      const skinUrl = 'https://mc-heads.net/avatar/' + p.nickname + '/64';\n"
    "      const isOnline = p.is_online\n"
    "        ? '<span style=\"display:inline-block;width:8px;height:8px;background:#4caf50;"
    "border-radius:50%;margin-right:5px;vertical-align:middle\"></span>'\n"
    "        : '';\n"
    "      return " + BT + "<div style=\"\n"
    "        display:flex;flex-direction:column;align-items:center;gap:10px;\n"
    "        background:var(--card,#1a1a1a);\n"
    "        border:1px solid var(--border,#2a2a2a);\n"
    "        border-radius:10px;padding:16px 12px;\n"
    "        width:180px;flex-shrink:0;\n"
    "        transition:border-color .2s;cursor:default;\n"
    "      \" onmouseover=\"this.style.borderColor='#c8a84b'\""
    " onmouseout=\"this.style.borderColor='var(--border,#2a2a2a)'\">\n"
    "        <img src=\"${skinUrl}\" alt=\"${p.nickname}\"\n"
    "          style=\"width:64px;height:64px;border-radius:6px;image-rendering:pixelated\"\n"
    "          onerror=\"this.src='https://mc-heads.net/avatar/Steve/64'\">\n"
    "        <div style=\"text-align:center\">\n"
    "          <div style=\"font-weight:600;font-size:14px\">${isOnline}${p.nickname}</div>\n"
    "          <div style=\"margin-top:6px;font-size:13px;color:var(--muted,#888)\">Наиграл:</div>\n"
    "          <div style=\"font-size:20px;font-weight:700;color:#c8a84b\">${hours}ч</div>\n"
    "        </div>\n"
    "      </div>" + BT + ";\n"
    "    }).join('');\n"
    "    tbl.innerHTML = '<div style=\"display:flex;flex-wrap:wrap;gap:12px;\">' + rows + '</div>';\n"
    "  }\n"
    "\n"
    "  async function loadStats() {\n"
    "    try {\n"
    "      const res = await fetch('/proxy.php?path=/web/server-stats');\n"
    "      if (res.ok) {\n"
    "        const data = await res.json();\n"
    "        document.getElementById('stat-online') && (document.getElementById('stat-online').textContent = data.online);\n"
    "        document.getElementById('nav-online').textContent = data.online + ' онлайн';\n"
    "        document.getElementById('hero-online').textContent = data.online;\n"
    "      }\n"
    "    } catch(e) { console.warn('server-stats error', e); }\n"
    "    try {\n"
    "      const res2 = await fetch('/proxy.php?path=/web/stats');\n"
    "      if (res2.ok) {\n"
    "        const leaders = await res2.json();\n"
    "        lastLeaders = leaders;\n"
    "        renderStatsTable(leaders);\n"
    "      }\n"
    "    } catch(e) { console.warn('stats error', e); }\n"
    "  }\n"
    "\n"
    "  function startStatsRefresh() {\n"
    "    if (statsRefreshTimer) clearInterval(statsRefreshTimer);\n"
    "    statsRefreshTimer = setInterval(loadStats, 30000);\n"
    "  }\n"
    "\n"
    "  function stopStatsRefresh() {\n"
    "    if (statsRefreshTimer) { clearInterval(statsRefreshTimer); statsRefreshTimer = null; }\n"
    "  }\n"
    "\n"
    "  // ── Communities ──────────────────────────────────────────────────────────\n"
    "  async function loadCommunities() {\n"
    "    const grid = document.getElementById('commGrid');\n"
    "    grid.innerHTML = '<div style=\"color:var(--muted);font-size:13px\">Загрузка...</div>';\n"
    "    try {\n"
    "      const res = await fetch('/proxy.php?path=/web/communities');\n"
    "      if (!res.ok) throw new Error('Ошибка загрузки');\n"
    "      const list = await res.json();\n"
    "      if (!list.length) {\n"
    "        grid.innerHTML = '<div style=\"color:var(--muted);font-size:13px\">Общин пока нет. Создайте первую!</div>';\n"
    "        return;\n"
    "      }\n"
    "      grid.innerHTML = list.map(c => " + BT + "\n"
    "        <div class=\"comm-card\">\n"
    "          <div class=\"comm-card-header\">\n"
    "            <div class=\"comm-icon\">${c.icon}</div>\n"
    "            <div>\n"
    "              <div class=\"comm-name\">${c.name}</div>\n"
    "              <div class=\"comm-members\">" + chr(0x1F465) + " ${c.member_count} "
    "участник${c.member_count===1?'':c.member_count<5?'а':'ов'}</div>\n"
    "            </div>\n"
    "          </div>\n"
    "          <p class=\"comm-desc\">${c.description}</p>\n"
    "          <div class=\"comm-footer\">\n"
    "            <span class=\"comm-tag\">${c.tag}</span>\n"
    "            <button class=\"comm-join\" onclick=\"joinCommunity(${c.id})\">Вступить</button>\n"
    "          </div>\n"
    "        </div>" + BT + ").join('');\n"
    "    } catch(e) {\n"
    "      grid.innerHTML = '<div style=\"color:var(--muted);font-size:13px\">Не удалось загрузить общины</div>';\n"
    "    }\n"
    "  }\n"
    "\n"
    "  async function joinCommunity(id) {\n"
    "    const user = JSON.parse(localStorage.getItem('discord_user') || 'null');\n"
    "    if (!user) { openModal('loginModal'); return; }\n"
    "    try {\n"
    "      const res = await fetch('/proxy.php?path=/web/communities/' + id + '/join', {\n"
    "        method: 'POST',\n"
    "        headers: { 'Content-Type': 'application/json' },\n"
    "        body: JSON.stringify({ discord_id: user.id }),\n"
    "      });\n"
    "      const data = await res.json();\n"
    "      if (!res.ok) { alert(data.detail || 'Ошибка вступления'); return; }\n"
    "      await loadCommunities();\n"
    "    } catch(e) { alert('Ошибка сети: ' + e.message); }\n"
    "  }\n"
    "\n"
)

if '</script>' in html:
    html = html.replace('</script>', NEW_JS + '</script>', 1)
    log.append("OK : All new JS functions injected before </script>")
else:
    log.append("ERR: </script> not found!")

# ════════════════════════════════════════════════════════════════════════════
# О. proxy.php уже пропускает /web/* — менять не нужно
# ════════════════════════════════════════════════════════════════════════════
log.append("SKP: proxy.php already allows /web/*")

# ── Загрузить на сервер ───────────────────────────────────────────────────
vps_host = "155.212.210.252"; vps_user = "root"; vps_pass = "2db**gLPoyDx"
beget_host = "REDACTED_HOST"; beget_user = "REDACTED_USER"; beget_pass = "REDACTED_PASSWORD"

jump = paramiko.SSHClient(); jump.set_missing_host_key_policy(paramiko.AutoAddPolicy())
jump.connect(vps_host, username=vps_user, password=vps_pass, timeout=15)
channel = jump.get_transport().open_channel("direct-tcpip", (beget_host, 22), ("127.0.0.1", 0))
target = paramiko.SSHClient(); target.set_missing_host_key_policy(paramiko.AutoAddPolicy())
target.connect(beget_host, username=beget_user, password=beget_pass, sock=channel, timeout=15)

encoded = html.encode("utf-8")
sftp = target.open_sftp()
with sftp.open('/home/k/katalist/gryazworld.ru/public_html/index.html', 'wb') as f:
    f.write(encoded)
sftp.close()
target.close(); jump.close()

log.append(f"OK : Uploaded {len(encoded):,} bytes to Beget")

for entry in log:
    sys.stdout.buffer.write(f"{entry}\n".encode('utf-8'))
