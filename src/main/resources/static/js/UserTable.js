document.querySelectorAll(".btnEditar").forEach((button) => {
  button.addEventListener("click", function () {
    const row = this.closest("tr");

    const userId = row
      .querySelector('form[action*="/delete/"]')
      .action.split("/")
      .pop();
    const userName = row.querySelector(".user-details h6").textContent;
    const userEmail = row.querySelector(".user-details small").textContent;
    const userAvatar = row.querySelector(".avatar").src;
    const currentRole = row.querySelector(".role-badge").textContent.trim();

    document.getElementById("userId").value = userId;
    document.getElementById("userName").textContent = userName;
    document.getElementById("userEmail").textContent = userEmail;
    document.getElementById("userAvatar").src = userAvatar;

    const selectRole = document.getElementById("userRole");
    Array.from(selectRole.options).forEach((option) => {
      if (option.text.trim() === currentRole || option.value === currentRole) {
        option.selected = true;
      }
    });

    const modal = new bootstrap.Modal(document.getElementById("editRoleModal"));
    modal.show();
  });
});
