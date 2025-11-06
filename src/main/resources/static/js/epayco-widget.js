var handler = ePayco.checkout.configure({
    key: 'a6cab5b1342bbbb93e223a9ba5b3519d',
    test: true
});

document.querySelectorAll('.plan-select-btn').forEach(function (btn) {
    btn.addEventListener('click', function () {
        const uniqueInvoice = btn.dataset.invoice + "-" + Date.now(); // 👈 evita conflictos

        handler.open({
            name: btn.dataset.name,
            description: btn.dataset.description,
            invoice: uniqueInvoice,
            currency: btn.dataset.currency,
            amount: btn.dataset.amount,
            tax_base: "0",
            tax: "0",
            country: "co",
            lang: "es",
            external: false
        });
    });
});
