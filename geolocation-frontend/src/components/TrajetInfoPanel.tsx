import { useState, useEffect } from "react";

interface TrajetInfo {
  idBus: string;
  immatriculation: string;
  ligne: {
    numeroLigne: string;
    nomLigne: string;
    couleur: string;
  } | null;
  direction: {
    nomDirection: string;
    pointDepart: string;
    pointArrivee: string;
  } | null;
  latitudeActuelle: number;
  longitudeActuelle: number;
  vitesseActuelle: number;
  derniereMiseAJour: string;
  heureDepart: string;
  distanceParcourue: number;
  dureeTrajetMinutes: number;
  nombreArretsEffectues: number;
  prochainArret?: string;
  distanceProchainArret?: number;
  tempsEstimeProchainArret?: number;
}

interface TrajetInfoPanelProps {
  busId: string;
  onClose: () => void;
}

const TrajetInfoPanel = ({ busId, onClose }: TrajetInfoPanelProps) => {
  const [trajetInfo, setTrajetInfo] = useState<TrajetInfo | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadTrajetInfo();
    // Rafraîchir toutes les 30 secondes
    const interval = setInterval(loadTrajetInfo, 30000);
    return () => clearInterval(interval);
  }, [busId]);

  const loadTrajetInfo = () => {
    setLoading(true);
    fetch(`http://localhost:8080/api/trajet/bus/${busId}`)
      .then((res) => {
        if (!res.ok) throw new Error("Erreur de chargement");
        return res.json();
      })
      .then((data) => {
        setTrajetInfo(data);
        setError(null);
      })
      .catch((err) => {
        console.error("Erreur:", err);
        setError("Impossible de charger les informations du trajet");
      })
      .finally(() => setLoading(false));
  };

  if (loading && !trajetInfo) {
    return (
      <div style={styles.panel}>
        <div style={styles.loading}>⏳ Chargement...</div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={styles.panel}>
        <button onClick={onClose} style={styles.closeButton}>✕</button>
        <div style={styles.error}>❌ {error}</div>
      </div>
    );
  }

  if (!trajetInfo) return null;

  return (
    <div style={styles.panel}>
      <div style={styles.header}>
        <h3 style={styles.title}>📊 Informations du Trajet</h3>
        <button onClick={onClose} style={styles.closeButton}>✕</button>
      </div>

      <div style={styles.content}>
        {/* Infos Bus */}
        <div style={styles.section}>
          <h4 style={styles.sectionTitle}>🚌 Bus</h4>
          <p><strong>Immatriculation:</strong> {trajetInfo.immatriculation}</p>
          {trajetInfo.ligne && (
            <p>
              <strong>Ligne:</strong>{" "}
              <span style={{ color: trajetInfo.ligne.couleur, fontWeight: "bold" }}>
                {trajetInfo.ligne.numeroLigne}
              </span>{" "}
              - {trajetInfo.ligne.nomLigne}
            </p>
          )}
          {trajetInfo.direction && (
            <p>
              <strong>Direction:</strong> {trajetInfo.direction.nomDirection}
              <br />
              <small>
                {trajetInfo.direction.pointDepart} → {trajetInfo.direction.pointArrivee}
              </small>
            </p>
          )}
        </div>

        {/* Position actuelle */}
        <div style={styles.section}>
          <h4 style={styles.sectionTitle}>📍 Position Actuelle</h4>
          <p><strong>Vitesse:</strong> {trajetInfo.vitesseActuelle.toFixed(1)} km/h</p>
          <p>
            <strong>Coordonnées:</strong><br />
            <small>
              Lat: {trajetInfo.latitudeActuelle.toFixed(6)}<br />
              Lng: {trajetInfo.longitudeActuelle.toFixed(6)}
            </small>
          </p>
          <p>
            <strong>Mise à jour:</strong>{" "}
            {new Date(trajetInfo.derniereMiseAJour).toLocaleTimeString()}
          </p>
        </div>

        {/* Statistiques du trajet */}
        <div style={styles.section}>
          <h4 style={styles.sectionTitle}>📈 Statistiques du Trajet</h4>
          <p>
            <strong>Heure de départ:</strong>{" "}
            {new Date(trajetInfo.heureDepart).toLocaleTimeString()}
          </p>
          <p>
            <strong>Distance parcourue:</strong> {trajetInfo.distanceParcourue} km
          </p>
          <p>
            <strong>Durée:</strong> {trajetInfo.dureeTrajetMinutes} minutes
          </p>
          <p>
            <strong>Arrêts effectués:</strong> {trajetInfo.nombreArretsEffectues}
          </p>
        </div>

        {/* Prochaine étape */}
        {trajetInfo.prochainArret && (
          <div style={styles.section}>
            <h4 style={styles.sectionTitle}>🎯 Prochaine Étape</h4>
            <p><strong>Arrêt:</strong> {trajetInfo.prochainArret}</p>
            {trajetInfo.distanceProchainArret && (
              <p><strong>Distance:</strong> {trajetInfo.distanceProchainArret} km</p>
            )}
            {trajetInfo.tempsEstimeProchainArret && (
              <p><strong>Temps estimé:</strong> {trajetInfo.tempsEstimeProchainArret} min</p>
            )}
          </div>
        )}
      </div>

      <button onClick={loadTrajetInfo} style={styles.refreshButton}>
        🔄 Actualiser
      </button>
    </div>
  );
};

const styles = {
  panel: {
    position: "absolute" as const,
    top: "20px",
    right: "20px",
    zIndex: 1000,
    backgroundColor: "white",
    borderRadius: "12px",
    boxShadow: "0 4px 20px rgba(0,0,0,0.2)",
    width: "380px",
    maxHeight: "90vh",
    overflow: "auto",
  },
  header: {
    display: "flex",
    justifyContent: "space-between",
    alignItems: "center",
    padding: "15px 20px",
    borderBottom: "2px solid #f0f0f0",
  },
  title: {
    margin: 0,
    fontSize: "18px",
    color: "#333",
  },
  closeButton: {
    background: "none",
    border: "none",
    fontSize: "24px",
    cursor: "pointer",
    color: "#999",
    padding: "0",
    width: "30px",
    height: "30px",
  },
  content: {
    padding: "20px",
  },
  section: {
    marginBottom: "20px",
    paddingBottom: "15px",
    borderBottom: "1px solid #eee",
  },
  sectionTitle: {
    margin: "0 0 10px 0",
    fontSize: "16px",
    color: "#007bff",
  },
  refreshButton: {
    width: "100%",
    padding: "12px",
    backgroundColor: "#28a745",
    color: "white",
    border: "none",
    borderRadius: "0 0 12px 12px",
    cursor: "pointer",
    fontWeight: "bold",
    fontSize: "14px",
  },
  loading: {
    padding: "40px",
    textAlign: "center" as const,
    fontSize: "16px",
  },
  error: {
    padding: "40px 20px",
    textAlign: "center" as const,
    color: "#dc3545",
  },
};

export default TrajetInfoPanel;