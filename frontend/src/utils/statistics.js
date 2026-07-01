/**
 * Takes a list of topics and puts at the first two positions the
 * two shortest titles, in ascending order and leaves the rest of
 * the array as is. Operates on the original array in place.
 * @param {Array} topics array of topics, topics must have the
 * type: { topicTitle: String }
 */
export function orderFirstThreePositionsByShortestTopicsTitle(topics) {
	if (!Array.isArray(topics) || topics.length === 0) {
		return topics;
	}

	// first shortest
	let minIndex1 = 0;
	for (let i = 1; i < topics.length; i++) {
		if (topics[i].topicTitle.length < topics[minIndex1].topicTitle.length) {
			minIndex1 = i;
		}
	}
	if (minIndex1 !== 0) {
		[topics[0], topics[minIndex1]] = [topics[minIndex1], topics[0]];
	}

	// second shortest
	let minIndex2 = 1;
	for (let i = 2; i < topics.length; i++) {
		if (topics[i].topicTitle.length < topics[minIndex2].topicTitle.length) {
			minIndex2 = i;
		}
	}
	if (minIndex2 !== 1) {
		[topics[1], topics[minIndex2]] = [topics[minIndex2], topics[1]];
	}

	// second shortest
	let minIndex3 = 2;
	for (let i = 3; i < topics.length; i++) {
		if (topics[i].topicTitle.length < topics[minIndex3].topicTitle.length) {
			minIndex3 = i;
		}
	}
	if (minIndex3 !== 2) {
		[topics[2], topics[minIndex3]] = [topics[minIndex3], topics[2]];
	}
}

/**
 * Takes a list of topics and moves the three longest titles to the
 * last three positions in ascending order of length,
 * with the longest title placed at the very end.
 * Operates on the original array in place.
 * @param {Array} topics array of topics, topics must have the
 * type: { topicTitle: String }
 */
export function orderLastThreePositionsByLongestTopicsTitle(topics) {
	if (!Array.isArray(topics) || topics.length < 3) {
		return topics;
	}

	// find longest title and return index
	const findLongest = (arr, limit) => {
		let maxIndex = 0;
		for (let i = 1; i < limit; i++) {
			if (arr[i].topicTitle.length > arr[maxIndex].topicTitle.length) {
				maxIndex = i;
			}
		}
		return maxIndex;
	};

	const len = topics.length;

	// longest last
	let longestIndex = findLongest(topics, len);
	if (longestIndex !== len - 1) {
		[topics[longestIndex], topics[len - 1]] = [
			topics[len - 1],
			topics[longestIndex],
		];
	}

	// second to longest
	let secondLongestIndex = findLongest(topics, len - 1);
	if (secondLongestIndex !== len - 2) {
		[topics[secondLongestIndex], topics[len - 2]] = [
			topics[len - 2],
			topics[secondLongestIndex],
		];
	}

	// third to longest
	let thirdLongestIndex = findLongest(topics, len - 2);
	if (thirdLongestIndex !== len - 3) {
		[topics[thirdLongestIndex], topics[len - 3]] = [
			topics[len - 3],
			topics[thirdLongestIndex],
		];
	}
}

/**
 * Takes a list of topics and reduces the topics title to a max length
 * with three dots after
 * @param {Number} MAX_LENGTH - Maximum allowed length for titles (including dots)
 * @param {Array} topics - Array of topics, each with shape: { topicTitle: String }
 */
export function shortenTopicsTitles(topics, MAX_LENGTH) {
	if (
		!Array.isArray(topics) ||
		topics.length === 0 ||
		typeof MAX_LENGTH !== 'number'
	) {
		return topics;
	}

	const DOTS = '...';

	for (let topic of topics) {
		if (
			typeof topic.topicTitle === 'string' &&
			topic.topicTitle.length > MAX_LENGTH
		) {
			const cutoff = MAX_LENGTH - DOTS.length;
			topic.topicTitle = topic.topicTitle.slice(0, cutoff) + DOTS;
		}
	}

	return topics;
}

/**
 * Takes a list of exercises and reduces the exercises title to a max length
 * with three dots after
 * @param {Number} MAX_LENGTH - Maximum allowed length for titles (including dots)
 * @param {Array} exercises - Array of exercises, each with shape: { exerciseTitle: String }
 */
export function shortenExercisesTitles(exercises, MAX_LENGTH) {
	if (
		!Array.isArray(exercises) ||
		exercises.length === 0 ||
		typeof MAX_LENGTH !== 'number'
	) {
		return exercises;
	}

	const DOTS = '...';

	for (let ex of exercises) {
		if (
			typeof ex.exerciseTitle === 'string' &&
			ex.exerciseTitle.length > MAX_LENGTH
		) {
			const cutoff = MAX_LENGTH - DOTS.length;
			ex.exerciseTitle = ex.exerciseTitle.slice(0, cutoff) + DOTS;
		}
	}

	return exercises;
}


export function sortQuestionsByTitle(
    stats
) {
    if(!Array.isArray(stats.questions) || stats.questions.length === 0) {
        return stats
    }
    stats.questions.sort((a, b) => a.questionTitle.localeCompare(b.questionTitle, undefined, { sensitivity: 'base' }));
    return stats;
}

export function sortByTitleReverse(
    stats
) {
    if(!Array.isArray(stats.questions) || stats.questions.length === 0) {
        return stats
    }
    stats.questions.sort((a, b) => b.questionTitle.localeCompare(a.questionTitle, undefined, { sensitivity: 'base' }));
    return stats
}


export function sortQuestionsByResult(
    stats
) {
    if(!Array.isArray(stats.questions) || stats.questions.length === 0) {
        return stats;
    }
    stats.questions.sort((a, b) => a.wrongAnswers - b.wrongAnswers);
    return stats;
}

export function sortByQuestionResultReverse(
    stats
) {
    if(!Array.isArray(stats.questions) || stats.questions.length === 0) {
        return stats;
    }
    stats.questions.sort((a, b) => b.wrongAnswers - a.wrongAnswers);
    return stats;
}

