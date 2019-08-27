import React, { Component, PureComponent } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import { connect } from "react-redux";
import { fetchSubmissionDetails } from "../store/submissionview/actions";
import { apiUrl } from "../utils/urls";
import axios from "axios";
import { formatSec } from "../utils/formatting";
import { Spring, animated } from "react-spring/renderprops";
import { IconContext } from "react-icons";
import { MdMusicNote, MdImage, MdVideocam } from "react-icons/md";

class SubmissionsBar extends PureComponent {
	render() {
		const { submissions } = this.props;
		return (
			<Box bg="background1" py={3}>
				<Flex className="horizontal-scroll no-scroll-bar">
					{submissions.map(submission => (
						<SubmissionBarItem
							key={submission.id}
							submission={submission}
							onClick={this.props.changeSubmission}
						/>
					))}
				</Flex>
			</Box>
		);
	}
}

const mapStateToProps = ({ projectViewReducer }) => ({
	submissions: projectViewReducer.submissions
});
const mapDispatchToProps = dispatch => ({
	changeSubmission: submissionId =>
		dispatch(fetchSubmissionDetails(submissionId))
});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(SubmissionsBar);

class SubmissionBarItem extends PureComponent {
	state = {
		progress: 0,
		sentencesMediaStatus: {}
	};
	async componentDidMount() {
		const { id } = this.props.submission;
		const { data } = await axios.get(
			`${apiUrl}/submissions/${id}/mediastatus`
		);
		const { sentencesMediaStatus } = data;
		const progress = this.calculateProgress(sentencesMediaStatus);
		this.setState({ progress, sentencesMediaStatus });
	}
	calculateProgress(mediaStatus) {
		const { audioStatus, slidesStatus, videoStatus } = mediaStatus;
		return (
			(100 *
				(audioStatus.generated +
					slidesStatus.generated +
					videoStatus.generated)) /
			(audioStatus.all + slidesStatus.all + videoStatus.all)
		);
	}
	isStatusReady(status) {
		try {
			return status.generated === status.all;
		} catch (err) {
			return false;
		}
	}
	render() {
		const {
			audioStatus,
			slidesStatus,
			videoStatus
		} = this.state.sentencesMediaStatus;
		return (
			<Box mx={2}>
				<SubmissionThumbnail
					{...this.props}
					audioReady={this.isStatusReady(audioStatus)}
					slidesReady={this.isStatusReady(slidesStatus)}
					videoReady={this.isStatusReady(videoStatus)}
				/>
				<ProgressBar progress={this.state.progress} />
			</Box>
		);
	}
}

const SubmissionThumbnail = props => {
	const { submission, onClick, audioReady, slidesReady, videoReady } = props;
	const imgSrc = `${apiUrl}/ui/submissions/${submission.id}/slide`;
	const width = 256;
	const height = 256 / (16 / 9);
	return (
		<Box
			{...props}
			style={{
				cursor: "pointer",
				minWidth: width,
				maxWidth: width,
				height,
				backgroundImage: `url(${imgSrc})`,
				backgroundSize: "cover"
			}}
			onClick={() => onClick(submission.id)}
		>
			<Flex flexDirection="column-reverse" style={{ height: "100%" }}>
				<Flex justifyContent="space-between">
					<Flex ml={2}>
						<ReadyIcon ready={audioReady}>
							<MdMusicNote />
						</ReadyIcon>
						<ReadyIcon mx={2} ready={slidesReady}>
							<MdImage />
						</ReadyIcon>
						<ReadyIcon ready={videoReady}>
							<MdVideocam />
						</ReadyIcon>
					</Flex>
					<Text m={1} color="text2">
						{formatSec(submission.audioDuration)}
					</Text>
				</Flex>
			</Flex>
		</Box>
	);
};
const ProgressBar = ({ progress }) => {
	const positive = { r: 67, g: 160, b: 71 };
	const negative = { r: 251, g: 192, b: 45 };
	const color = progress === 100 ? positive : negative;

	return (
		<Box bg="black">
			<Spring from={{ x: 0, ...negative }} to={{ x: progress, ...color }}>
				{({ x, r, g, b }) => (
					<animated.div
						style={{
							height: "7px",
							width: `${x}%`,
							backgroundColor: rgbStr(r, g, b)
						}}
					/>
				)}
			</Spring>
		</Box>
	);
};

const ReadyIcon = props => {
	const { ready, children } = props;
	return (
		<Box {...props}>
			<IconContext.Provider
				value={{ color: ready ? "#43a047" : "#fbc02d" }}
			>
				{children}
			</IconContext.Provider>
		</Box>
	);
};

const rgbStr = (r, g, b) => {
	return `rgb(${r},${g},${b})`;
};
