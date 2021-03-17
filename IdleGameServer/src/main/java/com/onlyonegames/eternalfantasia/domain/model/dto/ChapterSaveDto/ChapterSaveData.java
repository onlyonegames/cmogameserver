package com.onlyonegames.eternalfantasia.domain.model.dto.ChapterSaveDto;

import java.util.List;

public class ChapterSaveData {

    public static class StagePlayInfo
    {
        //스테이지 번호
        public int stageNo;
        //해당 스테이지 오픈 되었는지
        public boolean isOpend;
        //해당 스테이지 클리어 되었는지
        public boolean isCleared;
        //해당 스테이지 클리어 획득 별
        public boolean isGettedStar1; /*스테이지 클리어 하면 무조건 1 획득*/
        public boolean isGettedStar2; /*팀원 모두 살아 있으면 획득*/
        public boolean isGettedStar3; /*타임 제한 시간내 클리어하면 획득*/
    }

    public static class ChapterPlayInfo
    {
        //챕터 번호
        public int chapterNo;
        public boolean isOpend;
        public List<StagePlayInfo> stagePlayInfosList;
    }

    public static class ChapterData
    {
        public List<ChapterPlayInfo> chapterPlayInfosList;
    }

    public ChapterData chapterData;
}
