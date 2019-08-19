import { SET_DETAILS, CLEAR_DETAILS } from "./types";

const initialState = {
	title: "",
	duration: 0,
	submissions: []
};

export function projectDetailsReducer(state = initialState, action) {
	switch (action.type) {
		case SET_DETAILS: {
			const { title, duration, submissions } = action.details;
			return { ...state, title, duration, submissions };
		}

		case CLEAR_DETAILS: {
			return { ...state, title: "", duration: 0, submissions: [] };
		}

		default: {
			return state;
		}
	}
}
