const form = document.getElementById("upload-form");
const fileInput = document.getElementById("file-input");
const statusText = document.getElementById("status");
const titleEl = document.getElementById("movie-title");
const audioConfidenceEl = document.getElementById("audio-confidence");
const videoConfidenceEl = document.getElementById("video-confidence");
const movieInfoEl = document.getElementById("movie-info");

form.addEventListener("submit", async (e) => {
  e.preventDefault();

  const file = fileInput.files[0];

  if (!file) {
    statusText.innerText = "Please select a file!";
    return;
  }

  const formData = new FormData();
  formData.append("clip", file);

  try {
    statusText.innerText = "Uploading...";

    const response = await fetch("http://localhost:8000/api/media/match", {
      method: "POST",
      body: formData,
    });

    statusText.innerText = "Uploaded";
    //data received from backend
    const data = await response.json();
    sessionStorage.setItem("movieData", JSON.stringify(data));
    document.getElementById("top-match").classList.remove("hidden");
    document.getElementById("top-match").scrollIntoView({
      behavior: "smooth",
      block: "start",
    });

    console.log(data);

    //update text elements
    titleEl.innerText = data.finalDecision;
    const audioConfidence = Math.round(data.audioDetails.confidence);
    audioConfidenceEl.innerText = audioConfidence + "%";
    const videoConfidence = Math.round(data.videoDetails.confidence);
    videoConfidenceEl.innerText = videoConfidence + "%";
    movieInfoEl.innerText =
      data.yearOfRelease + " • " + data.genre + " • " + data.duration + "m";
  } catch (err) {
    console.error(err);
    statusText.innerText = "Upload failed!";
  } finally {
    document.getElementById("top-match").scrollIntoView({
      behavior: "smooth",
      block: "start",
    });
  }
});
