import { useState, useEffect } from "react";

interface LigneBus {
  idLigne: string;
  numeroLigne: string;
  nomLigne: string;
  couleur: string;
}

interface Direction {
  idDirection: string;
  nomDirection: string;
  pointDepart: string;
  pointArrivee: string;
}

interface BusSearchPanelProps {
  onSearch: (ligneId: string | null, directionId: string | null) => void;
}

const BusSearchPanel = ({ onSearch }: BusSearchPanelProps) => {
  const [lignes, setLignes] = useState<LigneBus[]>([]);
  const [directions, setDirections] = useState<Direction[]>([]);
  const [selectedLigne, setSelectedLigne] = useState<string>("");
  const [selectedDirection, setSelectedDirection] = useState<string>("");

  // Charger toutes les lignes au montage du composant
  useEffect(() => {
    fetch("http://localhost:8080/api/lignes/actives")
      .then((res) => res.json())
      .then((data) => setLignes(data))
      .catch((err) => console.error("Erreur de chargement des lignes :", err));
  }, []);

  // Charger les directions quand une ligne est sélectionnée
  useEffect(() => {
    if (selectedLigne) {
      fetch(`http://localhost:8080/api/directions/ligne/${selectedLigne}`)
        .then((res) => res.json())
        .then((data) => setDirections(data))
        .catch((err) => console.error("Erreur de chargement des directions :", err));
    } else {
      setDirections([]);
      setSelectedDirection("");
    }
  }, [selectedLigne]);

  const handleSearch = () => {
    const ligneId = selectedLigne || null;
    const directionId = selectedDirection || null;
    onSearch(ligneId, directionId);
  };

  const handleReset = () => {
    setSelectedLigne("");
    setSelectedDirection("");
    onSearch(null, null);
  };

  return (
    <div
      style={{
        position: "absolute",
        top: "20px",
        left: "20px",
        zIndex: 1000,
        backgroundColor: "white",
        padding: "20px",
        borderRadius: "10px",
        boxShadow: "0 4px 12px rgba(0,0,0,0.15)",
        minWidth: "320px",
        maxWidth: "400px",
      }}
    >
      <h3 style={{ margin: "0 0 15px 0", color: "#333", fontSize: "18px" }}>
        🔍 Recherche de Bus
      </h3>

      {/* Sélection de la ligne */}
      <div style={{ marginBottom: "15px" }}>
        <label
          htmlFor="ligne-select"
          style={{
            display: "block",
            marginBottom: "5px",
            fontWeight: "bold",
            color: "#555",
          }}
        >
          Ligne de Bus
        </label>
        <select
          id="ligne-select"
          value={selectedLigne}
          onChange={(e) => setSelectedLigne(e.target.value)}
          style={{
            width: "100%",
            padding: "10px",
            borderRadius: "5px",
            border: "1px solid #ddd",
            fontSize: "14px",
            cursor: "pointer",
          }}
        >
          <option value="">-- Toutes les lignes --</option>
          {lignes.map((ligne) => (
            <option key={ligne.idLigne} value={ligne.idLigne}>
              Ligne {ligne.numeroLigne} - {ligne.nomLigne}
            </option>
          ))}
        </select>
      </div>

      {/* Sélection de la direction */}
      <div style={{ marginBottom: "15px" }}>
        <label
          htmlFor="direction-select"
          style={{
            display: "block",
            marginBottom: "5px",
            fontWeight: "bold",
            color: "#555",
          }}
        >
          Direction
        </label>
        <select
          id="direction-select"
          value={selectedDirection}
          onChange={(e) => setSelectedDirection(e.target.value)}
          disabled={!selectedLigne}
          style={{
            width: "100%",
            padding: "10px",
            borderRadius: "5px",
            border: "1px solid #ddd",
            fontSize: "14px",
            cursor: selectedLigne ? "pointer" : "not-allowed",
            backgroundColor: selectedLigne ? "white" : "#f5f5f5",
          }}
        >
          <option value="">-- Toutes les directions --</option>
          {directions.map((direction) => (
            <option key={direction.idDirection} value={direction.idDirection}>
              {direction.nomDirection} ({direction.pointDepart} → {direction.pointArrivee})
            </option>
          ))}
        </select>
      </div>

      {/* Boutons d'action */}
      <div style={{ display: "flex", gap: "10px" }}>
        <button
          onClick={handleSearch}
          style={{
            flex: 1,
            padding: "10px",
            backgroundColor: "#007bff",
            color: "white",
            border: "none",
            borderRadius: "5px",
            cursor: "pointer",
            fontWeight: "bold",
            fontSize: "14px",
          }}
          onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#0056b3")}
          onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#007bff")}
        >
          🔎 Rechercher
        </button>
        <button
          onClick={handleReset}
          style={{
            flex: 1,
            padding: "10px",
            backgroundColor: "#6c757d",
            color: "white",
            border: "none",
            borderRadius: "5px",
            cursor: "pointer",
            fontWeight: "bold",
            fontSize: "14px",
          }}
          onMouseOver={(e) => (e.currentTarget.style.backgroundColor = "#5a6268")}
          onMouseOut={(e) => (e.currentTarget.style.backgroundColor = "#6c757d")}
        >
          ↺ Réinitialiser
        </button>
      </div>
    </div>
  );
};

export default BusSearchPanel;