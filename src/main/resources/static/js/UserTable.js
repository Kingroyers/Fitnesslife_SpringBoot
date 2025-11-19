// Script para abrir el modal y cargar los datos del usuario
document.querySelectorAll(".btnEditar").forEach((button) => {
  button.addEventListener("click", function () {
    const row = this.closest("tr");

    // Obtener datos del usuario desde la fila
    const userId = row
      .querySelector('form[action*="/delete/"]')
      .action.split("/")
      .pop();
    const userName = row.querySelector(".user-details h6").textContent;
    const userEmail = row.querySelector(".user-details small").textContent;
    const userAvatar = row.querySelector(".avatar").src;
    const currentRole = row.querySelector(".role-badge").textContent.trim();

    // Llenar el modal con los datos
    document.getElementById("userId").value = userId;
    document.getElementById("userName").textContent = userName;
    document.getElementById("userEmail").textContent = userEmail;
    document.getElementById("userAvatar").src = userAvatar;

    // Seleccionar el rol actual en el select
    const selectRole = document.getElementById("userRole");
    // Buscar y seleccionar el rol actual
    Array.from(selectRole.options).forEach((option) => {
      if (option.text.trim() === currentRole || option.value === currentRole) {
        option.selected = true;
      }
    });

    // Mostrar el modal
    const modal = new bootstrap.Modal(document.getElementById("editRoleModal"));
    modal.show();
  });
});
