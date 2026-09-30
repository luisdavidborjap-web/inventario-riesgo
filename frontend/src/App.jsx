import React, { useEffect, useState } from "react";

const api = async (url, options = {}) => {
  const res = await fetch(url, {
    headers: { "Content-Type": "application/json" },
    ...options,
  });
  if (!res.ok) throw new Error(await res.text());
  return res.json();
};

export default function App() {
  const [inventario, setInventario] = useState([]);
  const [recomendaciones, setRecomendaciones] = useState([]);
  const [metricas, setMetricas] = useState({});
  const [auditoria, setAuditoria] = useState([]);
  const [mensaje, setMensaje] = useState("");

  const cargar = async () => {
    const [i, r, m, a] = await Promise.all([
      api("/api/inventario"),
      api("/api/recomendaciones"),
      api("/api/metricas"),
      api("/api/auditoria"),
    ]);
    setInventario(i);
    setRecomendaciones(r);
    setMetricas(m);
    setAuditoria(a);
  };

  useEffect(() => { cargar(); }, []);

  const generar = async () => {
    try {
      const r = await api("/api/recomendaciones/generar", { method: "POST" });
      setMensaje(`Se generaron ${r.length} recomendación(es).`);
      await cargar();
    } catch (e) {
      setMensaje("Error: " + e.message);
    }
  };

  const decidir = async (id, accion) => {
    try {
      await api(`/api/recomendaciones/${id}/${accion}`, { method: "POST" });
      setMensaje(`Recomendación ${accion === "aprobar" ? "aprobada" : "rechazada"}.`);
      await cargar();
    } catch (e) {
      setMensaje("Error: " + e.message);
    }
  };

  const riesgo = (item) => {
    const dias = item.stock / Math.max(item.consumoPromedioDiario, 0.1);
    return dias <= 2 ? "ALTO" : dias <= 5 ? "MEDIO" : "BAJO";
  };

  return (
    <main>
      <header>
        <div>
          <h1>Inventario y riesgo de quiebre</h1>
          <p>Restaurantes · bodegas · pronóstico · aprobación humana</p>
        </div>
        <button className="primary" onClick={generar}>Generar recomendaciones</button>
      </header>

      {mensaje && <div className="notice">{mensaje}</div>}

      <section className="metrics">
        <article><span>Compras urgentes</span><strong>{metricas.comprasUrgentes ?? 0}</strong></article>
        <article><span>Aceptadas</span><strong>{Number(metricas.recomendacionesAceptadasPorcentaje ?? 0).toFixed(0)}%</strong></article>
        <article><span>Tiempo aprobación</span><strong>{Number(metricas.tiempoAprobacionPromedioMinutos ?? 0).toFixed(1)} min</strong></article>
        <article><span>Quiebres actuales</span><strong>{Object.values(metricas.quiebresPorProducto ?? {}).reduce((a,b)=>a+b,0)}</strong></article>
      </section>

      <section>
        <h2>Inventario</h2>
        <div className="table-wrap">
          <table>
            <thead><tr><th>Producto</th><th>Bodega</th><th>Stock</th><th>Consumo/día</th><th>Punto reposición</th><th>Riesgo</th></tr></thead>
            <tbody>
              {inventario.map(i => (
                <tr key={i.id}>
                  <td>{i.producto}</td><td>{i.bodega}</td><td>{i.stock}</td>
                  <td>{i.consumoPromedioDiario}</td><td>{i.puntoReposicion}</td>
                  <td><span className={`risk ${riesgo(i).toLowerCase()}`}>{riesgo(i)}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </section>

      <section>
        <h2>Recomendaciones</h2>
        {recomendaciones.length === 0 && <p>No hay recomendaciones. Presiona “Generar recomendaciones”.</p>}
        <div className="cards">
          {recomendaciones.map(r => (
            <article className="card" key={r.id}>
              <div className="card-top">
                <strong>{r.producto}</strong>
                <span className={`risk ${r.riesgo.toLowerCase()}`}>{r.riesgo}</span>
              </div>
              <p><b>Acción:</b> {r.tipo}</p>
              <p><b>Destino:</b> {r.bodegaDestino}</p>
              {r.bodegaOrigen && <p><b>Origen:</b> {r.bodegaOrigen}</p>}
              <p><b>Cantidad:</b> {r.cantidad}</p>
              <p>{r.motivo}</p>
              {r.usoFallback && <p className="fallback">Pronóstico en modo respaldo.</p>}
              <p><b>Estado:</b> {r.estado}</p>
              {r.estado === "PENDIENTE" && (
                <div className="actions">
                  <button className="approve" onClick={() => decidir(r.id, "aprobar")}>Aprobar</button>
                  <button className="reject" onClick={() => decidir(r.id, "rechazar")}>Rechazar</button>
                </div>
              )}
            </article>
          ))}
        </div>
      </section>

      <section>
        <h2>Auditoría</h2>
        <div className="audit">
          {auditoria.slice(0, 12).map(a => (
            <div key={a.id}>
              <strong>{a.accion}</strong>
              <span>{a.detalle}</span>
              <small>{new Date(a.fecha).toLocaleString()}</small>
            </div>
          ))}
        </div>
      </section>
    </main>
  );
}
