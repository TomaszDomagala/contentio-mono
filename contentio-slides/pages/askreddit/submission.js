import React, { Component } from "react";

import axios from "axios";
import Answer from "../../components/answer";
import Question from "../../components/question";

// id="CONTENT" on root submission div is for server.js screenshot checks 

class Submission extends Component {
	state = {
		fontInPixels: false,
		fontSize: 5
	};

	static async getInitialProps({ query: { id, slide } }) {
		const submissionPromise = axios.get(
			`http://127.0.0.1:8080/submissions/${id}`
		);
		const sentencesPromise = axios.get(
			`http://127.0.0.1:8080/submissions/${id}/sentences`
		);
		const [{ data: submission }, { data: sentences }] = await Promise.all([
			submissionPromise,
			sentencesPromise
		]);

		return { slide, submission, sentences };
	}

	componentDidMount() {
		this.adjustFontSize();
	}
	componentDidUpdate() {
		this.adjustFontSize();
	}
	adjustFontSize() {
		const { scrollHeight, clientHeight } = this.contentRef;
		console.log({ scrollHeight, clientHeight });
		if (
			scrollHeight > clientHeight &&
			this.props.submission.type === "COMMENT"
		) {
			if (this.state.fontInPixels) {
				const pxSize = this.state.fontSize;
				this.setState({ fontSize: this.decrementPx(pxSize) });
			} else if (this.state.fontSize === 1) {
				this.setState({ fontInPixels: true, fontSize: "13px" });
			} else {
				this.setState({ fontSize: this.state.fontSize - 1 });
			}
		}
	}
	decrementPx(px) {
		const size = px.substring(0, px.length - 2);
		return (parseInt(size) - 1).toString() + "px";
	}

	render() {
		const { submission, sentences, slide } = this.props;
		const content =
			submission.type === "COMMENT" ? (
				<Answer
					submission={submission}
					sentences={sentences}
					slide={slide}
					fontSize={this.state.fontSize}
				/>
			) : (
				<Question
					submission={submission}
					sentences={sentences}
					slide={slide}
					fontSize={this.state.fontSize}
				/>
			);
		return (
			<div id="CONTENT" ref={element => (this.contentRef = element)}>
				{content}
			</div>
		);
	}
}

export default Submission;
