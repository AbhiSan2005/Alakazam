// Get data from sessionStorage
const movieData = JSON.parse(sessionStorage.getItem("movieData"));

if (movieData) {
  // Update the audio elements
  document.getElementById("movie-title").textContent = movieData.finalDecision;
  document
    .getElementById("audio-confidence")
    .querySelector("span").textContent =
    Math.round(movieData.audioDetails.confidence) + "%";

  const audioProgressBar = document.querySelector(
    "#audio-confidence + div > div", // the inner div of the bar
  );
  if (audioProgressBar) {
    audioProgressBar.style.width = movieData.audioDetails.confidence + "%";
  }

  // Update the video elements
  document
    .getElementById("video-confidence")
    .querySelector("span").textContent =
    Math.round(movieData.videoDetails.confidence) + "%";

  const videoProgressBar = document.querySelector(
    "#video-confidence + div > div", // the inner div of the bar
  );
  if (videoProgressBar) {
    videoProgressBar.style.width = movieData.videoDetails.confidence + "%";
  }
} else {
  console.warn("No movie data found in sessionStorage!");
}
