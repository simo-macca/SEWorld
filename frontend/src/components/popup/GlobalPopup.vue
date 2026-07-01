<script>
// Default Constants
const OFFSET_TOP = 20;
const TYPES = ['success', 'warning', 'error', 'info'];

export default {
	name: 'GlobalPopup',

	data() {
		return {
			// holds popups
			popups: [],
			// holds timeouts of focused popup
			// id <-> time
			timeouts: {},
		};
	},

	methods: {
		show(title, message, type, duration) {
			if (!TYPES.find((t) => t == type)) {
				// type does not exist we default to info
				type = 'info';
			}

			const id = crypto.randomUUID();

			const popup = {
				id,
				title,
				message,
				type,
				visible: true,
				height: 0,
				removing: false,
			};

			// add popup to top
			this.popups.unshift(popup);

			// looping function that runs each time the dom is being updated
			// in this case we
			this.$nextTick(() => {
				// this is a js closure that saves the id of the popup
				// that called it
				const element = document.getElementById(`popup-${id}`);
				if (element) {
					// we add height dynamically as soon as it is rendered
					// plus the space between each popup
					popup.height = element.offsetHeight + 20;
					// then we upddate all positions at cascade
					this.updatePositions();
				}
			});

			// set timeout to hide the popup after duration
			this.setPopupTimeout(id, duration);
		},

		// sets thetimeout and updates popup timeouts by id
		setPopupTimeout(id, duration) {
			// clear any existing timeout
			if (this.timeouts[id]) {
				clearTimeout(this.timeouts[id].timeoutId);
			}

			// set new timeout
			this.timeouts[id] = {
				timeoutId: setTimeout(() => {
					this.removePopup(id);
				}, duration),
				startTime: Date.now(),
				remainingTime: duration,
			};
		},

		// handle enter mouse on popup
		handleMouseEnter(id) {
			const popup = this.popups.find((p) => p.id === id);
			if (popup && !popup.removing) {
				popup.isHovered = true;

				let timeout = this.timeouts[id];
				// clear the timeout
				if (timeout.timeoutId) {
					clearTimeout(timeout.timeoutId);
					timeout.timeoutId = null;
				}

				// the time that has passed in the timeout
				const elapsed = Date.now() - timeout.startTime;
				// subtract it to get the time it stil will have to persist once
				// the user exits the popup
				timeout.remainingTime -= elapsed;
			}
		},

		// handle mouse exit on popup
		handleMouseLeave(id) {
			const popup = this.popups.find((p) => p.id === id);
			if (popup && !popup.removing) {
				popup.isHovered = false;

				// Reset the timeout with remaining time
				this.setPopupTimeout(id, this.timeouts[id].remainingTime);
			}
		},

		// removes a popup smoothly from the UI, once disappeared
		// removes it from the popups list
		removePopup(id) {
			const index = this.popups.findIndex((p) => p.id === id);
			if (index !== -1 && !this.popups[index].removing) {
				if (this.timeouts[id]) {
					clearTimeout(this.timeouts[id]);
					delete this.timeouts[id];
				}

				// remove by triggering animation
				this.popups[index].removing = true;

				// give a timeout for it being removed correctly,
				// then we remove it from popups list
				setTimeout(() => {
					this.popups = this.popups.filter((p) => p.id !== id);
					this.updatePositions();
				}, 500);
			}
		},

		// update positions of all popups to show them properly
		updatePositions() {
			let offsetTop = OFFSET_TOP;

			// at cascade we translate each popup by their heights
			for (let i = 0; i < this.popups.length; i++) {
				// if it is hovered leave it where it is
				if (this.popups[i].isHovered) {
					// but moves offset by one
					offsetTop += this.popups[i].height;
					continue;
				}

				this.popups[i].top = offsetTop;
				offsetTop += this.popups[i].height;
			}
		},
	},
};
</script>

<template>
	<div class="popup-wrapper">
		<div
			v-for="popup in popups"
			:key="popup.id"
			:id="`popup-${popup.id}`"
			:class="[
				'popup',
				'position-fixed',
				'end-0',
				'rounded',
				'shadow-lg',
				`popup-${popup.type}`,
				{ 'popup-removing': popup.removing },
			]"
			:style="{
				top: `${popup.top}px`,
			}"
			@mouseenter="handleMouseEnter(popup.id)"
			@mouseleave="handleMouseLeave(popup.id)"
		>
			<div class="popup-header">
				<h1 class="fs-3">{{ popup.title }}</h1>
				<button class="close-btn" @click="removePopup(popup.id)">
					<span class="highlight">x</span>
				</button>
			</div>
			<p class="fs-6">{{ popup.message }}</p>
		</div>
	</div>
</template>

<style scoped>
@keyframes slideIn {
	0% {
		opacity: 0;
		transform: translateX(100%);
	}
	100% {
		opacity: 1;
		transform: translateX(0);
	}
}

@keyframes slideOut {
	0% {
		opacity: 1;
		transform: translateX(0);
	}
	100% {
		opacity: 0;
		transform: translateY(100%);
	}
}

/* Popup sheet style */
.popup-wrapper {
	z-index: 99999999;

	position: fixed;

	width: 100vw;
	height: 100vh;

	top: 0;
	right: 0;

	pointer-events: none;
}

.popup {
	width: 450px;
	min-height: 100px;

	background: var(--primary-bg-color);
	color: white;
	border-left: 4px solid white;
	border-top: 1px solid black;
	border-right: 1px solid black;
	border-bottom: 1px solid black;

	padding: 20px;
	margin: 8px 20px;

	pointer-events: auto;

	transition: all 0.5s ease;
	animation: slideIn 0.3s ease forwards;
}

.popup-removing {
	animation: slideOut 0.5s ease forwards;

	pointer-events: none;
}

.popup-header {
	display: flex;
	justify-content: space-between;
	align-items: center;

	margin-bottom: 10px;
}

.popup-header h1 {
	margin: 0;
}

.close-btn {
	background: none;
	border: none;

	font-size: 28px;
	line-height: 1;

	padding: 0 5px;

	cursor: pointer;

	display: flex;
	align-items: center;
	justify-content: center;
}

/* Messages Types */
.popup-success {
	border-left-color: #42b983;
}

.popup-error {
	border-left-color: #ff5252;
}

.popup-warning {
	border-left-color: #ffb62e;
}

.popup-info {
	border-left-color: #2196f3;
}

/* Smaller Screens Dispaly */
@media (max-width: 600px) {
	.popup {
		width: calc(100% - 40px);
		margin: 4px 10px;
		min-height: unset;
	}
}
</style>