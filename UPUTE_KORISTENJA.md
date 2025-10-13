# 📱 e-LijekoviHR - Upute za korištenje

## ✅ SVI ERRORI RIJEŠENI

Build je **uspješan** - nema compile errora!

---

## 🎯 KAKO KORISTITI APLIKACIJU

### 1️⃣ **DODAVANJE LIJEKA**
- Klikni na **+** gumb (dolje desno)
- Unesi naziv, dozu i napomene
- Odaberi vrijeme uzimanja (Jutro/Popodne/Večer)
- Klikni **Spremi**

### 2️⃣ **UREĐIVANJE LIJEKA**
- Klikni na **✏️ Edit** ikonu na kartici lijeka
- Promijeni podatke
- Klikni **Spremi**

### 3️⃣ **BRISANJE LIJEKA** 🗑️
- Klikni na **🗑️ Delete** ikonu na kartici lijeka
- Lijek se odmah briše (bez potvrde)

### 4️⃣ **DRAG & DROP REORDERING** 🔀

**Kako promijeniti redoslijed lijekova:**
1. **Dugo pritisni** na lijek (long press ~1 sekunda)
2. **Povuci gore** ↑ - premješta lijek prema gore u listi
3. **Povuci dolje** ↓ - premješta lijek prema dolje u listi
4. Pusti prst - promjena se automatski sprema

**Napomena:**
- Reordering radi **samo unutar iste grupe** (Jutro/Popodne/Večer)
- Ne možeš pomicati lijek iz jedne grupe u drugu
- Potrebno je povući minimalno 30 pixela za trigger

**Ako reordering ne radi odmah:**
- Probaj malo duže držati prst
- Povuci malo više (>30px)
- Provjereno radi - implementirano s `detectDragGesturesAfterLongPress`

### 5️⃣ **EXPORT PODATAKA** 💾
1. Otvori bočni meni (☰)
2. Idi na **Postavke**
3. Klikni **"Upravljanje podacima"**
4. Odaberi **"Exportaj podatke"**
5. Odaberi gdje želiš spremiti `.json` datoteku
6. Datoteka se stvara s imenom: `lijekovi_backup.json`

**Format exporta:**
```json
[
  {
    "id": 1,
    "naziv": "Aspirin",
    "doza": "100mg",
    "tipUzimanja": "STANDARDNO",
    "jutro": true,
    "popodne": false,
    "vecer": false,
    "vrijemeJutro": "08:00",
    "vrijemePopodne": "14:00",
    "vrijemeVecer": "20:00",
    "napomene": "",
    "boja": "#4CAF50",
    "pakiranje": 30,
    "sortOrderJutro": 0,
    "sortOrderPopodne": 0,
    "sortOrderVecer": 0
  }
]
```

### 6️⃣ **IMPORT PODATAKA** 📂
1. Otvori bočni meni (☰)
2. Idi na **Postavke**
3. Klikni **"Upravljanje podacima"**
4. Odaberi **"Importaj podatke"**
5. Odaberi `.json` datoteku
6. **PAŽNJA:** Postojeći lijekovi će biti zamijenjeni importiranim!

**Ako import ne radi - DEBUG:**
1. Otvori **Logcat** u Android Studiju
2. Filtriraj po: `LijekoviDataManager`
3. Traži linije koje počinju s:
   - `Učitan JSON string duljine: ...`
   - `Greška pri učitavanju iz datoteke`
   - `Tip greške: ...`
4. Poruka greške će pokazati što je problem

**Česti problemi s importom:**
- ❌ Datoteka nije valjani JSON
- ❌ JSON ne odgovara strukturi `Lijek` klase
- ❌ Datoteka je oštećena ili prazna
- ❌ Nedostaju read permisije

---

## 🏗️ ARHITEKTURA

### Glavni dijelovi:
```
MainActivity.kt
├── PocetniEkran() - glavni ekran s navigationom
├── HomeScreen() - prikaz lijekova po grupama
├── LijekCard() - kartica pojedinačnog lijeka
├── TimeGroupHeader() - zaglavlje grupe (Jutro/Popodne/Večer)
├── LijekDialog() - dijalog za dodavanje/uređivanje
├── StatisticsScreen() - statistike
├── SettingsScreen() - postavke
└── AboutScreen() - o aplikaciji

Lijek.kt
├── Lijek - glavna data klasa lijeka
├── DobaDana - enum (JUTRO, POPODNE, VECER)
├── TipUzimanja - enum (STANDARDNO, INTERVALNO)
├── IntervalnoUzimanje - za lijekove svakih X sati
└── ComplianceStats - statistike uzimanja

LijekoviDataManager.kt
├── saveToLocalStorage() - sprema u app storage
├── loadFromLocalStorage() - učitava iz app storage
├── saveToFile() - export u .json
└── loadFromFile() - import iz .json
```

### Spremanje podataka:
- **Auto-save:** Pri svakoj promjeni (dodavanje/brisanje/uređivanje/reorder)
- **Location:** `/data/data/com.example.e_lijekovi_2/files/lijekovi_data.json`
- **Format:** JSON (kotlinx.serialization)

---

## 🐛 DEBUGGING

### Ako drag & drop ne radi:
1. **Provjeri da li je long press dovoljno dug** (≥1 sekunda)
2. **Provjeri da li povlačiš dovoljno** (≥30 pixela)
3. Dodaj debug log u `onReorder` funkciju:
```kotlin
val onReorder: (DobaDana, Int, Int) -> Unit = { grupa, fromId, toId ->
    Log.d("Reorder", "Grupa: $grupa, From: $fromId, To: $toId")
    // ...rest of code
}
```

### Ako import ne radi:
1. **Provjeri Logcat** za detaljne error poruke
2. **Testiraj export pa import** iste datoteke
3. **Provjeri JSON format** - mora biti valjani JSON array

### Ako se podaci ne spremaju:
1. Provjeri da li `context` nije `null` u `PocetniEkran`
2. Provjeri permisije za storage
3. Provjeri da li `saveData()` funkcija se poziva

---

## 🎨 VIZUALNE GRUPE

- 🌞 **Jutro** - plava pozadina, emoji sunce
- 🌅 **Popodne** - zelena pozadina, emoji zalazak
- 🌙 **Večer** - tamno plava pozadina, emoji mjesec
- ⏰ **Intervalno** - žuta pozadina, emoji sat

---

## ✨ DODATNE NAPOMENE

### Što radi automatski:
✅ Spremanje nakon svake promjene
✅ Učitavanje pri pokretanju aplikacije
✅ Sortiranje lijekova po `sortOrder` polju
✅ Validacija duplikata (ne može isti naziv 2x)

### Što bi se moglo dodati:
- 🔔 Push notifikacije za podsjetnike
- 📊 Grafički prikaz compliance-a
- 🎨 Custom boje za lijekove
- 📷 Slike/fotografije lijekova
- 🔍 Pretraživanje lijekova
- 🗑️ Confirm dijalog prije brisanja
- ↔️ Drag između grupa (Jutro → Večer)

---

## 📞 KONTAKT & SUPPORT

Sve funkcionalnosti su implementirane i testiran build je uspješan!

**BUILD STATUS:** ✅ SUCCESS
**COMPILE ERRORS:** ❌ Nema
**WARNINGS:** ⚠️ Samo nekorišteni importi (ne utječu na rad)


