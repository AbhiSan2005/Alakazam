// Get data from sessionStorage
const movieData = JSON.parse(sessionStorage.getItem("movieData"));

if (movieData) {
  // Update the DOM
  document.getElementById("movie-title").textContent = movieData.mediaId;
  document.getElementById("confidence").querySelector("span").textContent =
    Math.round(movieData.confidence) + "%";

  // Optionally update the progress bar
  const progressBar = document.querySelector(
    "#confidence + div > div", // the inner div of the bar
  );
  if (progressBar) {
    progressBar.style.width = movieData.confidence + "%";
  }
} else {
  console.warn("No movie data found in sessionStorage!");
}
