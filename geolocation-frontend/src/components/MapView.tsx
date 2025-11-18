import { useEffect, useState } from "react";
import { MapContainer, TileLayer, Marker, Popup } from "react-leaflet";
import "leaflet/dist/leaflet.css";
import L from "leaflet";
import BusSearchPanel from "./BusSearchPanel";
import TrajetInfoPanel from "./TrajetInfoPanel";

// Icône personnalisée pour bus
const busIcon = new L.Icon({
  iconUrl: "src/assets/icons/bus.png",
  iconSize: [35, 35],
  iconAnchor: [17, 34],
  popupAnchor: [0, -30],
});

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

interface Bus {
  idBus: string;
  immatriculation: string;
  modele: string;
  marque: string;
  capacite: number;
  statut: string;
  ligneActuelle?: LigneBus;
  directionActuelle?: Direction;
}

interface PositionBus {
  idPosition: string;
  latitude: number;
  longitude: number;
  vitesse: number;
  timestamp: string;
  bus: Bus | null;
}

const MapView = () => {
  const [positions, setPositions] = useState<PositionBus[]>([]);
  const [filteredPositions, setFilteredPositions] = useState<PositionBus[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [selectedBusId, setSelectedBusId] = useState<string | null>(null);

  // Charger toutes les positions au démarrage
  useEffect(() => {
    console.log("🔄 Chargement initial des positions...");
    fetch("http://localhost:8080/api/positions")
      .then((res) => res.json())
      .then((data: PositionBus[]) => {
        console.log("✅ Positions brutes reçues:", data);

        // Filtrer les positions qui ont un bus valide
        const validPositions = data.filter(pos => pos.bus !== null && pos.bus !== undefined);
        console.log(`✅ Positions valides: ${validPositions.length} sur ${data.length}`);

        setPositions(validPositions);
        setFilteredPositions(validPositions);
      })
      .catch((err) => console.error("❌ Erreur de récupération des positions :", err));
  }, []);

  // Fonction de recherche appelée depuis le panneau de recherche
  const handleSearch = (ligneId: string | null, directionId: string | null) => {
    console.log("🔍 Recherche avec ligneId:", ligneId, "directionId:", directionId);

    if (!ligneId && !directionId) {
      // Aucun filtre : afficher tous les bus
      console.log("📍 Affichage de tous les bus");
      setFilteredPositions(positions);
      return;
    }

    setIsLoading(true);

    // Construire l'URL de recherche
    const params = new URLSearchParams();
    if (ligneId) params.append("ligneId", ligneId);
    if (directionId) params.append("directionId", directionId);

    const searchUrl = `http://localhost:8080/api/bus/search?${params.toString()}`;
    console.log("🌐 URL de recherche:", searchUrl);

    fetch(searchUrl)
      .then((res) => res.json())
      .then((buses: Bus[]) => {
        console.log("✅ Bus trouvés:", buses);
        console.log("📊 Nombre de bus trouvés:", buses.length);

        if (buses.length === 0) {
          console.warn("⚠️ Aucun bus trouvé pour ces critères");
          setFilteredPositions([]);
          setIsLoading(false);
          return;
        }

        // Filtrer les positions pour ne garder que celles des bus trouvés
        const busIds = buses.map((b) => b.idBus);
        console.log("🆔 IDs des bus trouvés:", busIds);

        const filtered = positions.filter((pos) => {
          // Vérifier que pos.bus existe
          if (!pos.bus || !pos.bus.idBus) {
            console.warn("⚠️ Position sans bus valide:", pos.idPosition);
            return false;
          }

          const match = busIds.includes(pos.bus.idBus);
          if (match) {
            console.log("✓ Bus correspondant:", pos.bus.immatriculation, pos.bus.idBus);
          }
          return match;
        });

        console.log(`📍 Positions filtrées: ${filtered.length} sur ${positions.length}`);
        setFilteredPositions(filtered);
        setIsLoading(false);
      })
      .catch((err) => {
        console.error("❌ Erreur de recherche:", err);
        setIsLoading(false);
      });
  };

  return (
    <div style={{ height: "100vh", width: "100%", position: "relative" }}>
      {/* Panneau de recherche */}
      <BusSearchPanel onSearch={handleSearch} />

      {/* Indicateur de chargement */}
      {isLoading && (
        <div
          style={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            zIndex: 2000,
            backgroundColor: "rgba(255, 255, 255, 0.95)",
            padding: "20px 40px",
            borderRadius: "10px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.3)",
            fontWeight: "bold",
            fontSize: "16px",
          }}
        >
          🔄 Recherche en cours...
        </div>
      )}

      {/* Carte */}
      <MapContainer
        center={[33.5731, -7.5898]} // Casablanca
        zoom={12}
        scrollWheelZoom={true}
        style={{ height: "100%", width: "100%" }}
      >
        <TileLayer
          attribution='&copy; <a href="https://www.openstreetmap.org/">OpenStreetMap</a>'
          url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
        />

        {/* Affichage des marqueurs de bus filtrés */}
        {filteredPositions.map((pos) => {
          // Double vérification de sécurité
          if (!pos.bus) return null;

          return (
            <Marker
              key={pos.idPosition}
              position={[pos.latitude, pos.longitude]}
              icon={busIcon}
            >
              <Popup>
                <div style={{ minWidth: "200px" }}>
                  <strong>🚌 Bus :</strong> {pos.bus.immatriculation || "Inconnu"} <br />
                  <strong>📦 Modèle :</strong> {pos.bus.modele || "N/A"} <br />
                  <strong>🏭 Marque :</strong> {pos.bus.marque || "N/A"} <br />
                  <strong>🚏 Ligne :</strong>{" "}
                  {pos.bus.ligneActuelle
                    ? `${pos.bus.ligneActuelle.numeroLigne} - ${pos.bus.ligneActuelle.nomLigne}`
                    : "Non assigné"}{" "}
                  <br />
                  <strong>➡️ Direction :</strong>{" "}
                  {pos.bus.directionActuelle
                    ? `${pos.bus.directionActuelle.nomDirection} (${pos.bus.directionActuelle.pointDepart} → ${pos.bus.directionActuelle.pointArrivee})`
                    : "Non assignée"}{" "}
                  <br />
                  <strong>⚡ Vitesse :</strong> {pos.vitesse} km/h <br />
                  <strong>📊 Statut :</strong> {pos.bus.statut || "Inconnu"} <br />
                  <strong>🕐 Horodatage :</strong>{" "}
                  {new Date(pos.timestamp).toLocaleString()}
                  <br />

                  {/* NOUVEAU BOUTON */}
                  <button
                    onClick={() => setSelectedBusId(pos.bus?.idBus || null)}
                    style={{
                      marginTop: "10px",
                      width: "100%",
                      padding: "8px",
                      backgroundColor: "#007bff",
                      color: "white",
                      border: "none",
                      borderRadius: "5px",
                      cursor: "pointer",
                      fontWeight: "bold",
                    }}
                  >
                    📊 Voir Détails du Trajet
                  </button>
                </div>
              </Popup>
            </Marker>
          );
        })}
      </MapContainer>

      {/* Message si aucun bus trouvé */}
      {!isLoading && filteredPositions.length === 0 && positions.length > 0 && (
        <div
          style={{
            position: "absolute",
            top: "50%",
            left: "50%",
            transform: "translate(-50%, -50%)",
            zIndex: 1500,
            backgroundColor: "rgba(255, 193, 7, 0.95)",
            padding: "20px 30px",
            borderRadius: "10px",
            boxShadow: "0 4px 12px rgba(0,0,0,0.3)",
            fontWeight: "bold",
            fontSize: "16px",
            textAlign: "center",
          }}
        >
          ⚠️ Aucun bus trouvé pour ces critères de recherche
        </div>
      )}

      {/* Compteur de bus affichés */}
      <div
        style={{
          position: "absolute",
          bottom: "20px",
          right: "20px",
          zIndex: 1000,
          backgroundColor: "rgba(255, 255, 255, 0.95)",
          padding: "15px 20px",
          borderRadius: "8px",
          boxShadow: "0 2px 8px rgba(0,0,0,0.2)",
          fontWeight: "bold",
          color: "#333",
          fontSize: "16px",
        }}
      >
        🚌 Bus affichés : <span style={{ color: "#007bff" }}>{filteredPositions.length}</span> / {positions.length}
      </div>

      {/* NOUVEAU PANNEAU D'INFORMATIONS DU TRAJET */}
      {selectedBusId && (
        <TrajetInfoPanel
          busId={selectedBusId}
          onClose={() => setSelectedBusId(null)}
        />
      )}
    </div>
  );
};

export default MapView;