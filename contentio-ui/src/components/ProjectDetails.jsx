import React, { Component, PureComponent } from "react";
import { Box, Flex, Card, Text, Heading, Image } from "rebass";
import ReactResizeDetector from "react-resize-detector";
import { connect } from "react-redux";
import { setCurrentSentence } from "../store/submissionview/actions";
import { apiUrl } from "../utils/urls";
import { formatSec } from "../utils/formatting";
import { IconContext } from "react-icons";
import { MdDone, MdInfoOutline } from "react-icons/md";

class ProjectDetails extends PureComponent {
	render() {
		const { title, audioDuration } = this.props.details;
		const { mediaStatus } = this.props;
		console.log(mediaStatus);
		const isMediaStatusEmpty =
			Object.entries(mediaStatus).length === 0 &&
			mediaStatus.constructor === Object;
		return (
			<Flex justifyContent="center">
				<Card
					p={2}
					m={[1, 0]}
					width={[1, 1 / 2]}
					bg="background2"
					borderColor="divider"
					borderStyle="solid"
					border={1}
					borderRadius={8}
				>
					<Heading fontSize={3} color="text2" mb={2}>
						{title}
					</Heading>
					<Text color="text2" fontSize={2}>
						Duration {formatSec(audioDuration)}
					</Text>
					{!isMediaStatusEmpty && (
						<Box>
							<MediaStatus
								media="Final video"
								status={mediaStatus.videoStatus}
							/>
							<MediaStatus
								media="Audio files"
								status={
									mediaStatus.sentencesMediaStatus.audioStatus
								}
							/>
							<MediaStatus
								media="Slide files"
								status={
									mediaStatus.sentencesMediaStatus
										.slidesStatus
								}
							/>
							<MediaStatus
								media="Video files"
								status={
									mediaStatus.sentencesMediaStatus.videoStatus
								}
							/>
						</Box>
					)}
				</Card>
			</Flex>
		);
	}
}

const mapStateToProps = ({ projectViewReducer }) => ({
	details: projectViewReducer.details,
	mediaStatus: projectViewReducer.mediaStatus
});
const mapDispatchToProps = dispatch => ({});

export default connect(
	mapStateToProps,
	mapDispatchToProps
)(ProjectDetails);

const MediaStatus = ({ media, status }) => {
	const { generated, missing, all } = status;
	let icon, color, message;
	if (generated === all) {
		icon = <MdDone />;
		color = "#4caf50";
		message = "";
	} else {
		icon = <MdInfoOutline />;
		color = "#fbc02d";
		message = `${missing} missing assets`;
	}
	return (
		<Flex>
			<IconContext.Provider value={{ color }}>
				{icon}
			</IconContext.Provider>
			<Text ml={2} color="text2">
				{media} {message}
			</Text>
		</Flex>
	);
};
