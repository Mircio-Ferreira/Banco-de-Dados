function addTelefone() {
    const input = document.getElementById("telefoneInput");
    const list = document.getElementById("telefoneList");

    if (input.value.trim() === "") return;

    const item = document.createElement("div");

    const text = document.createElement("span");
    text.textContent = input.value;

    const removeBtn = document.createElement("button");
    removeBtn.textContent = "X";
    removeBtn.style.marginLeft = "10px";

    removeBtn.onclick = () => {
        item.remove();
    };

    item.appendChild(text);
    item.appendChild(removeBtn);

    list.appendChild(item);
    input.value = "";
}