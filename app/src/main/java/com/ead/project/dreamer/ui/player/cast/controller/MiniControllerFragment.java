package com.ead.project.dreamer.ui.player.cast.controller;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import androidx.annotation.ColorInt;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.cast.framework.R.attr;
import com.google.android.gms.cast.framework.R.dimen;
import com.google.android.gms.cast.framework.R.drawable;
import com.google.android.gms.cast.framework.R.id;
import com.google.android.gms.cast.framework.R.layout;
import com.google.android.gms.cast.framework.R.string;
import com.google.android.gms.cast.framework.R.style;
import com.google.android.gms.cast.framework.R.styleable;
import com.google.android.gms.cast.framework.media.ImageHints;
import com.google.android.gms.cast.framework.media.uicontroller.UIMediaController;
import com.google.android.gms.cast.framework.media.widget.ControlButtonsContainer;
import com.google.android.gms.cast.internal.Logger;
import com.google.android.gms.common.internal.Preconditions;
import com.google.android.gms.internal.cast.zzkk;

@SuppressLint("WrongConstant")
public class MiniControllerFragment extends Fragment implements ControlButtonsContainer {
    private static final Logger zza;
    private boolean zzb;
    private int zzc;
    private int zzd;
    private TextView zze;
    private int zzf;
    private int zzg;
    @ColorInt
    private int zzh;
    private int zzi;
    private int[] zzj;
    private ImageView[] zzk = new ImageView[3];
    private int zzl;
    @DrawableRes
    private int zzm;
    @DrawableRes
    private int zzn;
    @DrawableRes
    private int zzo;
    @DrawableRes
    private int zzp;
    @DrawableRes
    private int zzq;
    @DrawableRes
    private int zzr;
    @DrawableRes
    private int zzs;
    @DrawableRes
    private int zzt;
    @DrawableRes
    private int zzu;
    @DrawableRes
    private int zzv;
    @DrawableRes
    private int zzw;
    @DrawableRes
    private int zzx;
    @Nullable
    private UIMediaController zzy;



    public final int getButtonSlotCount() {
        return 3;
    }

    public final int getButtonTypeAt(int slotIndex) throws IndexOutOfBoundsException {
        return this.zzj[slotIndex];
    }


    @NonNull
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle var3) {
        UIMediaController var10 = new UIMediaController(this.getActivity());
        this.zzy = var10;
        var10 = this.zzy;
        View inflater1 = inflater.inflate(layout.cast_mini_controller, container);
        inflater1.setVisibility(8);
        var10.bindViewVisibilityToMediaSession(inflater1, 8);
        RelativeLayout container1 = (RelativeLayout)inflater1.findViewById(id.container_current);
        container1.setPadding(16,0,8,16);
        int var4 = this.zzf;
        if (var4 != 0) {
            container1.setBackgroundResource(var4);
        }

        ImageView var11 = (ImageView)inflater1.findViewById(id.icon_view);
        TextView var5 = (TextView)inflater1.findViewById(id.title_view);
        if (this.zzc != 0) {
            var5.setTextAppearance(this.getActivity(), this.zzc);
        }

        this.zze = (TextView)inflater1.findViewById(id.subtitle_view);
        if (this.zzd != 0) {
            this.zze.setTextAppearance(this.getActivity(), this.zzd);
        }

        ProgressBar var6 = (ProgressBar)inflater1.findViewById(id.progressBar);
        if (this.zzg != 0) {
            ((LayerDrawable)var6.getProgressDrawable()).setColorFilter(this.zzg, Mode.SRC_IN);
        }

        var10.bindTextViewToMetadataOfCurrentItem(var5, "com.google.android.gms.cast.metadata.TITLE");
        var10.bindTextViewToSmartSubtitle(this.zze);
        var10.bindProgressBar(var6);
        var10.bindViewToLaunchExpandedController(container1);
        if (this.zzb) {
            Resources var12 = this.getResources();
            int var14 = dimen.cast_mini_controller_icon_width;
            int var13 = var12.getDimensionPixelSize(var14);
            Resources var15 = this.getResources();
            int var7 = dimen.cast_mini_controller_icon_height;
            var14 = var15.getDimensionPixelSize(var7);
            ImageHints var16 = new ImageHints(2, var13, var14);
            var10.bindImageViewToImageOfCurrentItem(var11, var16, drawable.cast_album_art_placeholder);
        } else {
            var11.setVisibility(8);
        }

        this.zzk[0] = (ImageView)container1.findViewById(id.button_0);
        this.zzk[1] = (ImageView)container1.findViewById(id.button_1);
        this.zzk[2] = (ImageView)container1.findViewById(id.button_2);
        this.zza(var10, container1, id.button_0, 0);
        this.zza(var10, container1, id.button_1, 1);
        this.zza(var10, container1, id.button_2, 2);
        return inflater1;
    }

    @NonNull
    public final ImageView getButtonImageViewAt(int slotIndex) throws IndexOutOfBoundsException {
        return this.zzk[slotIndex];
    }

    @Nullable
    public UIMediaController getUIMediaController() {
        return this.zzy;
    }

    static {
        Logger var0 = new Logger("MiniControllerFragment");
        zza = var0;
    }

    public MiniControllerFragment() {
    }

    public void onDestroy() {
        UIMediaController var1 = this.zzy;
        if (var1 != null) {
            var1.dispose();
            this.zzy = null;
        }

        super.onDestroy();
    }

    public void onInflate(@NonNull Context context, @NonNull AttributeSet attrs, @Nullable Bundle savedInstanceState) {
        super.onInflate(context, attrs, savedInstanceState);
        if (this.zzj == null) {
            TypedArray attrs1 = context.obtainStyledAttributes(attrs, styleable.CastMiniController, attr.castMiniControllerStyle, style.CastMiniController);
            this.zzb = attrs1.getBoolean(styleable.CastMiniController_castShowImageThumbnail, true);
            this.zzc = attrs1.getResourceId(styleable.CastMiniController_castTitleTextAppearance, 0);
            this.zzd = attrs1.getResourceId(styleable.CastMiniController_castSubtitleTextAppearance, 0);
            this.zzf = attrs1.getResourceId(styleable.CastMiniController_castBackground, 0);
            this.zzg = attrs1.getColor(styleable.CastMiniController_castProgressBarColor, 0);
            this.zzh = attrs1.getColor(styleable.CastMiniController_castMiniControllerLoadingIndicatorColor, this.zzg);
            this.zzi = attrs1.getResourceId(styleable.CastMiniController_castButtonColor, 0);
            this.zzm = attrs1.getResourceId(styleable.CastMiniController_castPlayButtonDrawable, 0);
            this.zzn = attrs1.getResourceId(styleable.CastMiniController_castPauseButtonDrawable, 0);
            this.zzo = attrs1.getResourceId(styleable.CastMiniController_castStopButtonDrawable, 0);
            this.zzp = attrs1.getResourceId(styleable.CastMiniController_castPlayButtonDrawable, 0);
            this.zzq = attrs1.getResourceId(styleable.CastMiniController_castPauseButtonDrawable, 0);
            this.zzr = attrs1.getResourceId(styleable.CastMiniController_castStopButtonDrawable, 0);
            this.zzs = attrs1.getResourceId(styleable.CastMiniController_castSkipPreviousButtonDrawable, 0);
            this.zzt = attrs1.getResourceId(styleable.CastMiniController_castSkipNextButtonDrawable, 0);
            this.zzu = attrs1.getResourceId(styleable.CastMiniController_castRewind30ButtonDrawable, 0);
            this.zzv = attrs1.getResourceId(styleable.CastMiniController_castForward30ButtonDrawable, 0);
            this.zzw = attrs1.getResourceId(styleable.CastMiniController_castMuteToggleButtonDrawable, 0);
            this.zzx = attrs1.getResourceId(styleable.CastMiniController_castClosedCaptionsButtonDrawable, 0);
            int savedInstanceState1 = attrs1.getResourceId(styleable.CastMiniController_castControlButtons, 0);
            if (savedInstanceState1 == 0) {
                zza.w("Unable to read attribute castControlButtons.", new Object[0]);
                int[] context3 = new int[]{id.cast_button_type_empty, id.cast_button_type_empty, id.cast_button_type_empty};
                this.zzj = context3;
            } else {
                TypedArray savedInstanceState2 = context.getResources().obtainTypedArray(savedInstanceState1);
                boolean context1;
                if (savedInstanceState2.length() == 3) {
                    context1 = true;
                } else {
                    context1 = false;
                }

                Preconditions.checkArgument(context1);
                this.zzj = new int[savedInstanceState2.length()];

                int context2;
                for(context2 = 0; context2 < savedInstanceState2.length(); ++context2) {
                    this.zzj[context2] = savedInstanceState2.getResourceId(context2, 0);
                }

                savedInstanceState2.recycle();
                if (this.zzb) {
                    this.zzj[0] = id.cast_button_type_empty;
                }

                this.zzl = 0;
                int[] savedInstanceState3 = this.zzj;
                int var4 = savedInstanceState3.length;

                for(context2 = 0; context2 < var4; ++context2) {
                    if (savedInstanceState3[context2] != id.cast_button_type_empty) {
                        ++this.zzl;
                    }
                }
            }

            attrs1.recycle();
        }

        com.google.android.gms.internal.cast.zzl.zzd(zzkk.zzd);
    }

    private final void zza(UIMediaController var1, RelativeLayout var2, int var3, int var4) {
        ImageView var7 = (ImageView)var2.findViewById(var3);
        var4 = this.zzj[var4];
        if (var4 == id.cast_button_type_empty) {
            var7.setVisibility(4);
        } else {
            if (var4 != id.cast_button_type_custom) {
                if (var4 == id.cast_button_type_play_pause_toggle) {
                    var4 = this.zzm;
                    int var5 = this.zzn;
                    int var6 = this.zzo;
                    if (this.zzl == 1) {
                        var4 = this.zzp;
                        var5 = this.zzq;
                        var6 = this.zzr;
                    }

                    Drawable var12 = com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, var4);
                    Drawable var13 = com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, var5);
                    Drawable var14 = com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, var6);
                    var7.setImageDrawable(var13);
                    ProgressBar var8 = new ProgressBar(this.getContext());
                    LayoutParams var9 = new LayoutParams(-2, -2);
                    var9.addRule(8, var3);
                    var9.addRule(6, var3);
                    var9.addRule(5, var3);
                    var9.addRule(7, var3);
                    var9.addRule(15);
                    var8.setLayoutParams(var9);
                    var8.setVisibility(8);
                    Drawable var11 = var8.getIndeterminateDrawable();
                    int var10 = this.zzh;
                    if (var10 != 0 && var11 != null) {
                        var11.setColorFilter(var10, Mode.SRC_IN);
                    }

                    var2.addView(var8);
                    var1.bindImageViewToPlayPauseToggle(var7, var12, var13, var14, var8, true);
                    return;
                }

                if (var4 == id.cast_button_type_skip_previous) {
                    var7.setImageDrawable(com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, this.zzs));
                    var7.setContentDescription(this.getResources().getString(string.cast_skip_prev));
                    var1.bindViewToSkipPrev(var7, 0);
                    return;
                }

                if (var4 == id.cast_button_type_skip_next) {
                    var7.setImageDrawable(com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, this.zzt));
                    var7.setContentDescription(this.getResources().getString(string.cast_skip_next));
                    var1.bindViewToSkipNext(var7, 0);
                    return;
                }

                if (var4 == id.cast_button_type_rewind_30_seconds) {
                    var7.setImageDrawable(com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, this.zzu));
                    var7.setContentDescription(this.getResources().getString(string.cast_rewind_30));
                    var1.bindViewToRewind(var7, 30000L);
                    return;
                }

                if (var4 == id.cast_button_type_forward_30_seconds) {
                    var7.setImageDrawable(com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, this.zzv));
                    var7.setContentDescription(this.getResources().getString(string.cast_forward_30));
                    var1.bindViewToForward(var7, 30000L);
                    return;
                }

                if (var4 == id.cast_button_type_mute_toggle) {
                    var7.setImageDrawable(com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, this.zzw));
                    var1.bindImageViewToMuteToggle(var7);
                    return;
                }

                if (var4 == id.cast_button_type_closed_caption) {
                    var7.setImageDrawable(com.google.android.gms.cast.framework.media.widget.zzr.zzc(this.getContext(), this.zzi, this.zzx));
                    var1.bindViewToClosedCaption(var7);
                    return;
                }
            }

        }
    }
}
