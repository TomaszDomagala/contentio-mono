import { SET_DETAILS, CLEAR_DETAILS } from "./types";

const initialState = {
	title: "",
	predictedDuration: 0,
	audioDuration: 0,
	submissions: []
};

export function projectDetailsReducer(state = initialState, action) {
	switch (action.type) {
		case SET_DETAILS: {
			const {
				title,
				predictedDuration,
				audioDuration,
				submissions
			} = action.details;
			return {
				...state,
				title,
				predictedDuration,
				audioDuration,
				submissions
			};
		}

		case CLEAR_DETAILS: {
			return { ...state, title: "", duration: 0, submissions: [] };
		}

		default: {
			return state;
		}
	}
}
