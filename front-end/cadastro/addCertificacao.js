function addCert() {
    const input = document.getElementById("certInput");
    const list = document.getElementById("certList");

    if (input.value.trim() === "") return;

    const item = document.createElement("div");

    const text = document.createElement("span");
    text.textContent = input.value;

    const removeBtn = document.createElement("button");
    removeBtn.textContent = "❌";
    removeBtn.style.marginLeft = "10px";

    removeBtn.onclick = () => {
        item.remove();
    };

    item.appendChild(text);
    item.appendChild(removeBtn);

    list.appendChild(item);
    input.value = "";
}