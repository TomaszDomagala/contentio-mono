export const formatSec = time => {
	const { minutes, seconds } = secToMinSec(time);
	return `${minutes}:${seconds.toFixed(0).padStart(2, "0")}`;
};

export const secToMinSec = time => {
	const minutes = Math.floor(time / 60);
	const seconds = time - minutes * 60;
	return { minutes, seconds };
};
