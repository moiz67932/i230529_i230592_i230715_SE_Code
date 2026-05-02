(() => {
    const root = document.documentElement;
    const storageKey = "pipelinex-theme";

    const applyTheme = theme => {
        if (theme === "dark") {
            root.setAttribute("data-theme", "dark");
        } else {
            root.removeAttribute("data-theme");
        }
    };

    const savedTheme = localStorage.getItem(storageKey);
    applyTheme(savedTheme === "dark" ? "dark" : "light");

    document.addEventListener("click", event => {
        const button = event.target.closest("#themeToggle");
        if (!button) {
            return;
        }
        const isDark = root.getAttribute("data-theme") === "dark";
        const nextTheme = isDark ? "light" : "dark";
        localStorage.setItem(storageKey, nextTheme);
        applyTheme(nextTheme);
    });
})();
