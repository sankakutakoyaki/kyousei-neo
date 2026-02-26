"use strict"

function required(message, focus = false) {
    return {
        focus,
        test: v => v.trim() !== "",
        message
    };
}

function notValue(disallowed, message) {
    return {
        test: v => v !== disallowed,
        message
    };
}

function range(min, max, message) {
    return {
        test: v => {
            const n = Number(v);
            return !Number.isNaN(n) && n >= min && n <= max;
        },
        message
    };
}

function rule(selector, ...checks) {
    return {
        selector,
        checks
    };
}

function phone(message) {
    return {
        test: v => !v || /^\d{10,11}$/.test(v.replaceAll('-', '')),
        message
    };
}

function email(message) {
    return {
        test: v => !v || /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v),
        message
    };
}

function postalCode(message) {
    return {
        test: v => !v || /^\d{3}-?\d{4}$/.test(v),
        message
    };
}

function webAddress(message) {
    return {
        test: v => {
            if (!v) return true;
            try {
                new URL(v.startsWith("http") ? v : "https://" + v);
                return true;
            } catch {
                return false;
            }
        },
        message
    };
}

function dateAfterOrEqual(targetSelector, message) {
    return {
        test: (v, el) => {
            if (!v) return true;

            const area = el.closest("form");
            const baseEl = area?.querySelector(targetSelector);
            const baseValue = baseEl?.value;

            if (!baseValue) return true;

            return new Date(v) >= new Date(baseValue);
        },
        message
    };
}

function validateByConfig(area, config, mode = null) {
    const messages = [];
    let focusTarget = null;

    const commonRules = config.common ?? [];
    const modeRules =
        mode && config.modes?.[mode]
            ? config.modes[mode]
            : config.rules ?? [];

    const rules = [...commonRules, ...modeRules];

    for (const rule of rules) {
        const el = area.querySelector(rule.selector);
        if (!el) continue;

        const value = el.value ?? "";

        for (const check of rule.checks) {
            if (!check.test(value, el)) {
                messages.push(check.message);

                if (!focusTarget && check.focus) {
                    focusTarget = el;
                }

                break;
            }
        }
    }

    if (messages.length > 0) {
        openMsgDialog("msg-dialog", messages.join("\n"), "red");

        if (focusTarget) {
            setFocusElement("msg-dialog", focusTarget);
        }

        return false;
    }

    return true;
}