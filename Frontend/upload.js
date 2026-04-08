const form = document.getElementById("upload-form");
const fileInput = document.getElementById("file-input");
const statusText = document.getElementById("status");
const titleEl = document.getElementById("movie-title");
const confidenceEl = document.getElementById("confidence");

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

    const response = await fetch("http://localhost:8000/api/video/match", {
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
    titleEl.innerText = data.mediaId;
    const confidence = Math.round(data.confidence);
    confidenceEl.innerText = confidence + "%";
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
