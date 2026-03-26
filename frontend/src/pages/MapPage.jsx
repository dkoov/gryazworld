import './MapPage.css'

export default function MapPage() {
  return (
    <section className="section">
      <div className="section-label">Карта</div>
      <div className="section-title">Карта мира</div>
      <p className="section-sub">Интерактивная карта сервера в реальном времени.</p>

      <div className="map-notice">
        <strong>Для разработчика:</strong> карта генерируется плагином <strong>Dynmap</strong> или <strong>BlueMap</strong> на Minecraft-сервере. После установки замените блок ниже на iframe с адресом плагина.
      </div>

      {/* Replace with: <iframe src="http://YOUR_IP:8123" className="map-iframe" title="Server Map" /> */}
      <div className="map-frame">
        <div className="map-icon">&#x1F5FA;</div>
        <p>
          Карта появится после установки плагина<br />
          <strong>Dynmap</strong> или <strong>BlueMap</strong> на Minecraft-сервер
        </p>
      </div>
    </section>
  )
}
