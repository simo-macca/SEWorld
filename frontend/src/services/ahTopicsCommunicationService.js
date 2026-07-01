import { reactive } from 'vue'

export const ahTopicsCommunicationService = reactive({
  activeTopicsDid: [],
  hoveredTopicsDid: [],

  // Active
  addActiveTopic(newDid) {
    this.activeTopicsDid.push(newDid);
    this.hoveredTopicsDid = this.hoveredTopicsDid.filter(did =>
      did != newDid);
  },
  removeActiveTopic(newDid) {
    this.activeTopicsDid = this.activeTopicsDid.filter(did =>
      did != newDid
    );
  },

  // Hover
  addHoveredTopic(newDid) {
    this.hoveredTopicsDid.push(newDid);
  },
  removeHoveredTopic(newDid) {
    this.hoveredTopicsDid = this.hoveredTopicsDid.filter(did =>
      did != newDid
    );
  },

  // clear
  clearActiveTopic() {
    this.activeTopicsDid = [];
  },
  clearHoveredTopic() {
    this.hoveredTopicsDid = [];
  },
  clearAllTopics() {
    this.clearActiveTopic();
    this.clearHoveredTopic();
  },
})