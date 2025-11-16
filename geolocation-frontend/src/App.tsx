import React, { useEffect } from "react";
import MapView from "./components/MapView";

function App() {
  // (Facultatif) Vérifie la connexion au backend dans la console uniquement
  useEffect(() => {
    fetch("http://localhost:8080/api/hello")
      .then((res) => res.text())
      .then((data) => console.log("✅ Backend connecté :", data))
      .catch((err) => console.error("❌ Erreur connexion backend :", err));
  }, []);

  return (
    <div
      style={{
        height: "100vh",
        width: "100vw",
        margin: 0,
        padding: 0,
        overflow: "hidden",
      }}
    >
      <MapView />
    </div>
  );
}

export default App;