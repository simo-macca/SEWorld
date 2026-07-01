let popupInstance = null;

const DURATION_MS = 7000;

export function registerGlobalPopup(instance) {
	popupInstance = instance;
}

// throws a success popup with title and message
export function successPopup(
	title = 'Success',
	message = 'Operation executed successfully',
) {
	showPopup(title, message, 'success', 2200);
}

// throws an error popup with title and message
export function errorPopup(
	title = 'Error',
	message = 'Operation throw an error',
) {
	showPopup(title, message, 'error');
}

// throws a warning popup with title and message
export function warningPopup(title = 'Warning', message = 'Do not try again') {
	showPopup(title, message, 'warning');
}

// throws an info popup with title and message
export function infoPopup(title = 'Info', message = 'Information message') {
	showPopup(title, message, 'info');
}

export function showPopup(
	title = 'Info',
	message = 'Information message',
	type = 'info',
	duration = DURATION_MS,
) {
	if (popupInstance) {
		popupInstance.show(title, message, type, duration);
	} else {
		console.warn('Popup instance not registered yet');
	}
}
